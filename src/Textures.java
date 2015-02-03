import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;
import framework.JOGLFrame;
import framework.Scene;


/**
 * Display a simple scene to demonstrate OpenGL.
 *
 * @author Robert C. Duvall
 */
public class Textures extends Scene {
    private static float[] LIGHT0_POINT = { 2, 0, 4, 1 };
    private static float[] LIGHT0_COLOR = { 1, 1, 1, 1 };
    private static String[] TEXTURE_FILES = {
        "/images/earth.png",
        "/images/checkerboard.jpg",
        "/images/skybox_rt.rgb"
    };
    // animation state
    private float myAngle;
    private Texture[] myTextures;
    private Texture myCurrentTexture;
    private boolean isSphere;


    /**
     * Create the scene with the given arguments.
     */
    public Textures (String[] args) {
        super("Textures");
    }

    /**
     * Initialize general OpenGL values once (in place of constructor).
     */
    @Override
    public void init (GL2 gl, GLU glu, GLUT glut) {
        myAngle = 0;
        // load textures from disk ONCE
        myTextures = new Texture[TEXTURE_FILES.length];
        for (int k = 0; k < TEXTURE_FILES.length; k++) {
            myTextures[k] = makeTexture(gl, TEXTURE_FILES[k]);
        }
        myCurrentTexture = myTextures[0];
        isSphere = false;
    }

    /**
     * Draw all of the objects to display.
     */
    @Override
    public void display (GL2 gl, GLU glu, GLUT glut) {
        gl.glRotatef(myAngle, 0, 1, 0);
        myCurrentTexture.enable(gl);
        myCurrentTexture.bind(gl);
        makeGeometry(gl, glu, glut);
        myCurrentTexture.disable(gl);
    }

    /**
     * Animate the scene by changing its state slightly.
     */
    @Override
    public void animate (GL2 gl, GLU glu, GLUT glut) {
        // animate model by spinning it a few degrees each time
        myAngle += 1;
    }

    /**
     * Set the camera's view of the scene.
     */
    @Override
    public void setCamera (GL2 gl, GLU glu, GLUT glut) {
        glu.gluLookAt(0, 0, 4,  // from position
                      0, 0, 0,  // to position
                      0, 1, 0); // up direction
    }

    /**
     * Establish lights in the scene.
     */
    public void setLighting (GL2 gl, GLU glu, GLUT glut) {
        // turn bare bones lighting on
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        // position and color light
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, LIGHT0_POINT, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, LIGHT0_COLOR, 0);
    }

    /**
     * Called when any key is pressed within the canvas. Turns each part of the arm separately.
     */
    @Override
    public void keyReleased (int keyCode) {
        switch (keyCode) {
          // turn light on/off
          case KeyEvent.VK_S:
            isSphere = true;
            break;
          case KeyEvent.VK_R:
            isSphere = false;
            break;
          case KeyEvent.VK_1:
          case KeyEvent.VK_2:
          case KeyEvent.VK_3:
            myCurrentTexture = myTextures[keyCode - KeyEvent.VK_1];
            break;
        }
    }

    // choose geometry to display
    private void makeGeometry (GL2 gl, GLU glu, GLUT glut) {
        if (isSphere) {
            makeSphere(gl, glu, glut);            
        }
        else {
            makePlane(gl, glu, glut);
        }
    }

    // set up plane with texture coordinates and a normal
    private void makePlane (GL2 gl, GLU glu, GLUT glut) {
        TextureCoords coords = myCurrentTexture.getImageTexCoords();
        gl.glBegin(GL2.GL_QUADS); {
            gl.glNormal3f(0, 0, 1);
            gl.glTexCoord2f(coords.left(), coords.top());
            gl.glVertex3f(-1, 1, 0);
            gl.glTexCoord2f(coords.right(), coords.top());
            gl.glVertex3f(1, 1, 0);
            gl.glTexCoord2f(coords.right(), coords.bottom());
            gl.glVertex3f(1, -1, 0);
            gl.glTexCoord2f(coords.left(), coords.bottom());
            gl.glVertex3f(-1, -1, 0);
        }
        gl.glEnd();
    }

    // set up sphere with texture coordinates
    private void makeSphere (GL2 gl, GLU glu, GLUT glut) {
        gl.glRotatef(-90, 1, 0, 0);
        GLUquadric quad = glu.gluNewQuadric();
        glu.gluQuadricTexture(quad, true);
        glu.gluSphere(quad, 1, 20, 20);
    }

    // boilerplate to make a texture
    private Texture makeTexture (GL2 gl, String name) {
        try {
            String suffix = name.substring(name.lastIndexOf(".") + 1);
            Texture result = TextureIO.newTexture(getClass().getResourceAsStream(name), false, suffix);
            // set automatically by JOGL?
            //result.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            //result.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
            //result.setTexParameterf(gl, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_BLEND);
            return result;
        } catch (IOException e) {
            System.err.println("Unable to load texture image: " + name);
            System.exit(1);
            // should never happen
            return null;
        }
    }


    // allow program to be run from here
    public static void main (String[] args) {
        new JOGLFrame(new Textures(args));
    }
}
