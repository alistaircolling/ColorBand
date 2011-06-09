import java.util.Iterator;

import com.sun.tools.corba.se.idl.toJavaPortable.TCOffsets;

import processing.core.PApplet;

import processing.opengl.*;

import toxi.math.waves.SineWave;
import toxi.physics2d.behaviors.*;
import toxi.physics2d.*;
import toxi.geom.*;
import toxi.color.*;

public class ColorBand extends PApplet {

	private static final int MAX_PARTICLES = 30;
	private static final float SPRING_LENGTH = 5;
	private VerletPhysics2D physics;
	private int continuous;
	private int current;
	private VerletParticle2D prev;
	private Vec2D currPos;
	private SineWave wave;
	private SineWave wave2;
	private float startX = 100;
	private Vec2D ranStart;
	private int col;
	private boolean mouseDown;

	public void setup() {
		size(1280, 720, OPENGL);
		frameRate(45);
		println("hi!!");
		col = 0;
		ranStart = new Vec2D(round(random(width)), round(random(height)));
		wave = new SineWave(0, 0.02f, width * .5f, .001f);
		wave2 = new SineWave(0, 0.02f, height * .5f, .1f);
		physics = new VerletPhysics2D();
		physics.addBehavior(new GravityBehavior(new Vec2D(0f, 0.2f)));
		strokeWeight(40);

	}

	public void draw() {
		
		if (mousePressed){
			updatePosition(mouseX, mouseY);
		}else{
			updatePosition(width*.5f+wave.update(), 100);
		}

		background(40);
		// wave.update();

		physics.update();
		for (VerletSpring2D s : physics.springs) {
			float currHue = map(s.b.sub(s.a).heading(), -PI, PI, 0, 1);
			stroke(TColor.newHSV(currHue, 1, 1).toARGB());
			line(s.a.x, s.a.y, s.b.x, s.b.y);
		}
		removeOffscreen();
		
	}

	private void removeOffscreen() {
		for (Iterator<VerletSpring2D> i = physics.springs.iterator(); i
				.hasNext();) {
			VerletSpring2D s = i.next();
			if (s.a.y > height + 100 || s.b.y > height + 100) {
				i.remove();
			}
		}
		for (int i = physics.particles.size() - 1; i >= 0; i--) {
			VerletParticle2D p = physics.particles.get(i);
			if (p.y > height + 200) {
				try {
					physics.removeParticle(p);
					ParticleBehavior2D b = physics.behaviors.get(i);
					physics.removeBehavior(b);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}
	}

	public void mouseDragged() {
		// println("mouse dragged:"+);
		float hue = map(height / mouseY, -PI, PI, 0, 1);
		println(hue);
		col = TColor.newHSV(hue, 1, 1).toARGB();

	}

	private void updatePosition( float posX, float posY) {
		//println(physics.particles.size());
//		if (physics.particles.size() % MAX_PARTICLES == 0) {
//			endLine();
//		}
		VerletParticle2D p = new VerletParticle2D(posX, posY);
	//	VerletParticle2D p = new VerletParticle2D(ranStart.x+ wave.update(), 
	//			ranStart.y+ wave2.update());
		p.lock();

		if (physics.particles.size() > 0 && continuous == current) {
			prev = physics.particles.get(physics.particles.size() - 1);
			VerletSpring2D s = new VerletSpring2D(p, prev, SPRING_LENGTH, 1);
			physics.addSpring(s);
		} else {
			current = continuous;
		}
		// unlock previous particle
		if (prev != null) {
			prev.unlock();
		}
		physics.addParticle(p);
		// create a forcefield with a radius of 20 and force -1.5 aka push
		AttractionBehavior b = new AttractionBehavior(p, 20, (float) -1.5);
	}

	private void endLine() {
		// println("ended");
		ranStart = new Vec2D(round(random(width)), round(random(height)));
		if (prev != null) {
			prev.unlock();
		}
		continuous++;

		// TODO Auto-generated method stub

	}

	public void mouseReleased() {
		mouseDown = false;
	}

	public void mousePressed() {
		mouseDown = true;
	}

	public void keyPressed() {
		println("key pressed:" + key);
	}

}
