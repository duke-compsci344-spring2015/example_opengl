import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import framework.JOGLFrame;
import framework.Scene;


/**
 * Display a simple scene to demonstrate OpenGL.
 *
 * @author Robert C. Duvall
 */
public class SelectGrid extends Scene {
    // animation state
    private float myAngle;
    private int myNumRows;
    private float[][][] myColors;


    public SelectGrid (String[] args) {
        super("Select Grid");
    }

    /**
     * Initialize general OpenGL values once (in place of constructor).
     */
    @Override
    public void init (GL2 gl, GLU glu, GLUT glut) {
        myAngle = 0;
        myNumRows = 3;
        myColors = new float[myNumRows][myNumRows][4];
        for (int r = 0; r < myNumRows; r++) {
            myColors[r] = new float[myNumRows][4];
            for (int c = 0; c < myNumRows; c++) {
                myColors[r][c] = new float[4];
                myColors[r][c][0] = (c % 3) == 0 ? 1 : 0;
                myColors[r][c][1] = (c % 3) == 1 ? 1 : 0;
                myColors[r][c][2] = (c % 3) == 2 ? 1 : 0;
                myColors[r][c][2] = 1;
            }
        }
    }

    /**
     * Draw all of the objects to display.
     */
    @Override
    public void display (GL2 gl, GLU glu, GLUT glut) {
        gl.glRotatef(myAngle, 1, 0, 0);
        // keep objects sorted so closest is always drawn first
        int start = 0;
        int end = myNumRows;
        int incr = 1;
        if (myAngle % 360 > 180) {
            start = myNumRows - 1;
            end = -1;
            incr = -1;
        }
        // make grid of objects to view each spinning
        float half = (myNumRows - 1) / 2.0f;
        for (int r = start; r != end; r += incr) {
            // name objects according to location in grid
            gl.glLoadName(r);
            for (int c = start; c != end; c += incr) {
                gl.glPushName(c); {
                    // color including alpha
                    gl.glColor4fv(myColors[r][c], 0);
                    gl.glPushMatrix(); {
                        gl.glTranslatef((r-half)*2 / myNumRows, (c-half)*2 / myNumRows, 0);
                        gl.glRotatef(myAngle, 0, 1, 0);
                        glut.glutSolidCube(0.8f / myNumRows);
                    }
                    gl.glPopMatrix();
                }
                gl.glPopName();
            }
        }
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
        glu.gluLookAt(0, 0, 2.5, // from position
                      0, 0, 0,   // to position
                      0, 1, 0);  // up direction
    }

    /**
     * Called when the mouse is pressed within the canvas and it hits something.
     */
    @Override
    public void selectObject (GL2 gl, GLU glu, GLUT glut, int numSelected, int[] selectInfo) {
        // guaranteed at least one was selected
        int minZ = selectInfo[1];
        int r = selectInfo[3];
        int c = selectInfo[4];
        // for each hit, find closest object
        int idx = 5;
        for (int k = 1; k < numSelected; k++, idx += 5) {
            if (selectInfo[idx + 1] < minZ) {
                minZ = selectInfo[idx + 1];
                r = selectInfo[idx + 3];
                c = selectInfo[idx + 4];
            }
        }
        // update color of selected object
        myColors[r][c][2] -= 0.1;
        myColors[r][c][0] += 0.1;
        System.out.println(numSelected + ": chose [" + r + ", " + c + "]");
    }


    // allow program to be run from here
    public static void main (String[] args) {
        new JOGLFrame(new SelectGrid(args));
    }
}
