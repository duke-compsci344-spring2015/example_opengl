import java.awt.event.KeyEvent;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import framework.Scene;
import framework.JOGLFrame;
import framework.Material;


/**
 * Display a simple scene to demonstrate OpenGL.
 * 
 * @author Robert C. Duvall
 */
public class Lights extends Scene {
    // materials
    private Material[] materials = Material.values();
    // lighting state
    private float[] myLightPos = { 0, 0,  1, 1 };
    private float[] myLightDir = { 0, 0, -1, 1 };
    // model state
    private int myNumRows;
    private boolean isLightOn;
    private boolean isSmooth;
    private float mySpotAngle;    
    private int myNumPolys;
    // animation state
    private float myAngle;


    public Lights (String[] args) {
        super("Grid of Lit Materials");
        myNumRows = (args.length > 0) ? Integer.parseInt(args[0]) : 4;
        isLightOn = false;
        isSmooth = false;
        mySpotAngle = 60;
        myNumPolys = 16;
        myAngle = 0;
    }

    /**
     * Initialize general OpenGL values once (in place of constructor).
     */
    public void init (GL2 gl, GLU glu, GLUT glut) {
    }

    /**
     * Draw all of the objects to display.
     */
    public void display (GL2 gl, GLU glu, GLUT glut) {
        // make grid of objects to view each spinning
        float half = (myNumRows - 1) / 2.0f;
        for (int r = 0; r < myNumRows; r++) {
            for (int c = 0; c < myNumRows; c++) {
                Material m = materials[(r * myNumRows + c) % materials.length];
                if (isLightOn) m.set(gl);
                else           m.setAsColor(gl);                    
                gl.glPushMatrix(); {
                    gl.glTranslatef((c-half)*2 / myNumRows, -(r-half)*2 / myNumRows, 0.0f);
                    gl.glRotatef(myAngle, 0.0f, 1.0f, 0.0f);
                    // GLUT automatically defines normals
                    glut.glutSolidSphere(0.8 / myNumRows, myNumPolys, myNumPolys);
                }
                gl.glPopMatrix();
            }
        }
    }

    /**
     * Animate the scene by changing its state slightly.
     */
    @Override
    public void animate (GL2 gl, GLU glu, GLUT glut) {
        // animate model by spinning it
        myAngle += 1.0f;
    }

    /**
     * Set the camera's view of the scene.
     */
    public void setCamera (GL2 gl, GLU glu, GLUT glut) {
        glu.gluLookAt(0, 0, 2.4, // from position
                      0, 0, 0,   // to position
                      0, 1, 0);  // up direction
    }

    /**
     * Establish the lights in the scene.
     */
    public void setLighting (GL2 gl, GLU glu, GLUT glut) {
        // interpolate color on objects across polygons or not
        if (isSmooth) {
            gl.glShadeModel(GL2.GL_SMOOTH);
        }
        else {
            gl.glShadeModel(GL2.GL_FLAT);            
        }
        // turn one light on or off
        if (isLightOn) {
            gl.glEnable(GL2.GL_LIGHTING);
            gl.glEnable(GL2.GL_LIGHT0);
        }
        else {
            gl.glDisable(GL2.GL_LIGHTING);
        }
        // position light and make it a spotlight
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, myLightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPOT_DIRECTION, myLightDir, 0);
        gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF, mySpotAngle);
    }

    /**
     * Called when any key is pressed within the canvas. Turns each part of the arm separately.
     */
    @Override
    public void keyReleased (int keyCode) {
        switch (keyCode) {
          // turn light on/off
          case KeyEvent.VK_L:
            isLightOn = ! isLightOn;
            break;
          case KeyEvent.VK_S:
            isSmooth = ! isSmooth;
            break;
          case KeyEvent.VK_PLUS:
          case KeyEvent.VK_EQUALS:
            mySpotAngle += 5;
            break;
          case KeyEvent.VK_MINUS:
          case KeyEvent.VK_UNDERSCORE:
            mySpotAngle -= 5;
            break;
          case KeyEvent.VK_LESS:
          case KeyEvent.VK_COMMA:
            myNumPolys -= 1;
            break;
          case KeyEvent.VK_GREATER:
          case KeyEvent.VK_PERIOD:
            myNumPolys += 1;
            break;
        }
    }


    // allow program to be run from here
    public static void main (String[] args) {
        new JOGLFrame(new Lights(args));
    }
}
