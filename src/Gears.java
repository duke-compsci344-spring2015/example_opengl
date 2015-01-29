import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import framework.Scene;
import framework.JOGLFrame;


/**
 * Some fun spinning gears.
 * 
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 * 
 * @author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel)
 * @author: Robert C. Duvall (converted to my framework)
 */
public class Gears extends Scene {
    private int gear1;
    private int gear2;
    private int gear3;
    private float angle = 0.0f;

    public Gears (String[] args) {
        super("Gear Demo");
    }

    /**
     * Initialize general OpenGL values once (in place of constructor).
     */
    public void init (GL2 gl, GLU glu, GLUT glut) {
        float red[] = { 0.8f, 0.1f, 0.0f, 1.0f };
        float green[] = { 0.0f, 0.8f, 0.2f, 1.0f };
        float blue[] = { 0.2f, 0.2f, 1.0f, 1.0f };

        // make the gears
        gear1 = gl.glGenLists(1);
        gl.glNewList(gear1, GL2.GL_COMPILE);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, red, 0);
        gear(gl, 1.0f, 4.0f, 1.0f, 20, 0.7f);
        gl.glEndList();

        gear2 = gl.glGenLists(1);
        gl.glNewList(gear2, GL2.GL_COMPILE);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, green, 0);
        gear(gl, 0.5f, 2.0f, 2.0f, 10, 0.7f);
        gl.glEndList();

        gear3 = gl.glGenLists(1);
        gl.glNewList(gear3, GL2.GL_COMPILE);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT_AND_DIFFUSE, blue, 0);
        gear(gl, 1.3f, 2.0f, 0.5f, 10, 0.7f);
        gl.glEndList();
    }

    /**
     * Draw all of the objects to display.
     */
    public void display (GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix(); {
            gl.glTranslatef(-3.0f, -2.0f, 0.0f);
            gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
            // gear(gl, 1.0f, 4.0f, 1.0f, 20, 0.7f);
            gl.glCallList(gear1);
        }
        gl.glPopMatrix();

        gl.glPushMatrix(); {
            gl.glTranslatef(3.1f, -2.0f, 0.0f);
            gl.glRotatef(-2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f);
            // gear(gl, 0.5f, 2.0f, 2.0f, 10, 0.7f);
            gl.glCallList(gear2);
        }
        gl.glPopMatrix();

        gl.glPushMatrix(); {
            gl.glTranslatef(-3.1f, 4.2f, 0.0f);
            gl.glRotatef(-2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f);
            // gear(gl, 1.3f, 2.0f, 0.5f, 10, 0.7f);
            gl.glCallList(gear3);
        }
        gl.glPopMatrix();
    }

    /**
     * Animate the scene by changing its state slightly.
     */
    public void animate (GL2 gl, GLU glu, GLUT glut) {
        // animate model by spinning it a few degrees each time
        angle += 2;
    }

    /**
     * Set the camera's view of the scene.
     */
    public void setCamera (GL2 gl, GLU glu, GLUT glut) {
        glu.gluLookAt(4, 8, 16, // from position
                      0, 0, 0,  // to position
                      0, 1, 0); // up direction
    }

    /**
     * Establish the lights in the scene.
     */
    public void setLighting (GL2 gl, GLU glu, GLUT glut) {
        // turn on lighting
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        // position light
        float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, pos, 0);
    }

    // create geometry for gear
    public static void gear (GL2 gl,
                             float inner_radius,
                             float outer_radius,
                             float width,
                             int teeth,
                             float tooth_depth) {
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);

        float r0 = inner_radius;
        float r1 = outer_radius - tooth_depth / 2.0f;
        float r2 = outer_radius + tooth_depth / 2.0f;
        float da = 2 * (float)Math.PI / teeth / 4.0f;
        // draw front face
        gl.glBegin(GL2.GL_QUAD_STRIP); {
            for (int i = 0; i <= teeth; i++) {
                float angle = i * 2 * (float)Math.PI / teeth;
                gl.glVertex3f(r0 * (float)Math.cos(angle),
                              r0 * (float)Math.sin(angle),
                              width * 0.5f);
                gl.glVertex3f(r1 * (float)Math.cos(angle),
                              r1 * (float)Math.sin(angle),
                              width * 0.5f);
                if (i < teeth) {
                    gl.glVertex3f(r0 * (float)Math.cos(angle),
                                  r0 * (float)Math.sin(angle),
                                  width * 0.5f);
                    gl.glVertex3f(r1 * (float)Math.cos(angle + 3.0f * da),
                                  r1 * (float)Math.sin(angle + 3.0f * da),
                                  width * 0.5f);
                }
            }
        }
        gl.glEnd();

        // draw front sides of teeth
        gl.glBegin(GL2.GL_QUADS); {
            for (int i = 0; i < teeth; i++) {
                float angle = i * 2 * (float)Math.PI / teeth;
                gl.glVertex3f(r1 * (float)Math.cos(angle),
                              r1 * (float)Math.sin(angle),
                              width * 0.5f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + da),
                              r2 * (float)Math.sin(angle + da),
                              width * 0.5f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + 2.0f * da),
                              r2 * (float)Math.sin(angle + 2.0f * da),
                              width * 0.5f);
                gl.glVertex3f(r1 * (float)Math.cos(angle + 3.0f * da),
                              r1 * (float)Math.sin(angle + 3.0f * da),
                              width * 0.5f);
            }
        }
        gl.glEnd();

        // draw back face
        gl.glBegin(GL2.GL_QUAD_STRIP); {
            for (int i = 0; i <= teeth; i++) {
                float angle = i * 2 * (float)Math.PI / teeth;
                gl.glVertex3f(r1 * (float)Math.cos(angle),
                              r1 * (float)Math.sin(angle),
                              -width * 0.5f);
                gl.glVertex3f(r0 * (float)Math.cos(angle),
                              r0 * (float)Math.sin(angle),
                              -width * 0.5f);
                gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da),
                              r1 * (float)Math.sin(angle + 3 * da),
                              -width * 0.5f);
                gl.glVertex3f(r0 * (float)Math.cos(angle),
                              r0 * (float)Math.sin(angle),
                              -width * 0.5f);
            }
        }
        gl.glEnd();

        // draw back sides of teeth
        gl.glBegin(GL2.GL_QUADS); {
            for (int i = 0; i < teeth; i++) {
                float angle = i * 2 * (float)Math.PI / teeth;
                gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da),
                              r1 * (float)Math.sin(angle + 3 * da),
                              -width * 0.5f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + 2 * da),
                              r2 * (float)Math.sin(angle + 2 * da),
                              -width * 0.5f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + da),
                              r2 * (float) Math.sin(angle + da),
                              -width * 0.5f);
                gl.glVertex3f(r1 * (float)Math.cos(angle),
                              r1 * (float)Math.sin(angle),
                              -width * 0.5f);
            }
        }
        gl.glEnd();

        // draw outward faces of teeth
        gl.glBegin(GL2.GL_QUAD_STRIP); {
            for (int i = 0; i < teeth; i++) {
                float angle = i * 2 * (float)Math.PI / teeth;
                gl.glVertex3f(r1 * (float)Math.cos(angle),
                              r1 * (float)Math.sin(angle),
                              width * 0.5f);
                gl.glVertex3f(r1 * (float)Math.cos(angle),
                              r1 * (float)Math.sin(angle),
                              -width * 0.5f);
                float u = r2 * (float)Math.cos(angle + da) - r1 * (float)Math.cos(angle);
                float v = r2 * (float)Math.sin(angle + da) - r1 * (float)Math.sin(angle);
                float len = (float)Math.sqrt(u * u + v * v);
                u /= len;
                v /= len;
                gl.glNormal3f(v, -u, 0.0f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + da),
                              r2 * (float)Math.sin(angle + da),
                              width * 0.5f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + da),
                              r2 * (float)Math.sin(angle + da),
                              -width * 0.5f);
                gl.glNormal3f((float)Math.cos(angle),
                              (float)Math.sin(angle),
                              0.0f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + 2 * da),
                              r2 * (float)Math.sin(angle + 2 * da),
                              width * 0.5f);
                gl.glVertex3f(r2 * (float)Math.cos(angle + 2 * da),
                              r2 * (float)Math.sin(angle + 2 * da),
                              -width * 0.5f);
                u = r1 * (float)Math.cos(angle + 3 * da) - r2 * (float)Math.cos(angle + 2 * da);
                v = r1 * (float)Math.sin(angle + 3 * da) - r2 * (float)Math.sin(angle + 2 * da);
                gl.glNormal3f(v, -u, 0.0f);
                gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da),
                              r1 * (float)Math.sin(angle + 3 * da),
                              width * 0.5f);
                gl.glVertex3f(r1 * (float)Math.cos(angle + 3 * da),
                              r1 * (float)Math.sin(angle + 3 * da),
                              -width * 0.5f);
                gl.glNormal3f((float)Math.cos(angle), (float)Math.sin(angle), 0.0f);
            }
            gl.glVertex3f(r1 * (float)Math.cos(0),
                          r1 * (float)Math.sin(0),
                          width * 0.5f);
            gl.glVertex3f(r1 * (float)Math.cos(0),
                          r1 * (float)Math.sin(0),
                          -width * 0.5f);
        }
        gl.glEnd();

        gl.glShadeModel(GL2.GL_SMOOTH);
        // draw inside radius cylinder
        gl.glBegin(GL2.GL_QUAD_STRIP); {
            for (int i = 0; i <= teeth; i++) {
                float angle = i * 2 * (float)Math.PI / teeth;
                gl.glNormal3f(-(float)Math.cos(angle),
                              -(float)Math.sin(angle),
                              0.0f);
                gl.glVertex3f(r0 * (float)Math.cos(angle),
                              r0 * (float)Math.sin(angle),
                              -width * 0.5f);
                gl.glVertex3f(r0 * (float)Math.cos(angle),
                              r0 * (float) Math.sin(angle),
                              width * 0.5f);
            }
        }
        gl.glEnd();
    }


    // allow program to be run from here
    public static void main (String[] args) {
        new JOGLFrame(new Gears(args));
    }
}
