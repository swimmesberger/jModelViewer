package org.fseek.thedeath.modelview;

import org.fseek.thedeath.modelview.interfaces.IDownloader;
import org.fseek.thedeath.modelview.interfaces.IAnimChangedListener;
import org.fseek.thedeath.modelview.interfaces.IGraphicContext;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.Screenshot;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import org.fseek.thedeath.modelview.util.Util;

/**
 *
 * @author Thedeath<www.fseek.org>
 */
public class ModelViewer implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, FileRequester, IDownloader, IGraphicContext, IAnimChangedListener
{

    public static final int MT_ITEM = 1;
    public static final int MT_HELM = 2;
    public static final int MT_SHOULDER = 4;
    public static final int MT_NPC = 8;
    public static final int MT_CHAR = 16;
    public static final int MT_HUMAN_NPC = 32;
    public static final int MT_OBJECT = 64;
    private boolean caching = true;
    private File cacheDir = new File(Util.getMainFile() + File.separator + "cache");
    private String animation;
    private String argv[];
    private FPSAnimator animator;
    private boolean fileDownloading = false;
    private Texture logoTexture;
    private InputStream logoTextureStream;
    private int currMouseX;
    private int currMouseY;
    private int prevMouseX;
    private int prevMouseY;
    private boolean mouseRButtonDown;
    private boolean mouseLButtonDown;
    private boolean mouseFromLButton;
    private Camera cam;
    private Model model;
    private String newModel = null;
    private String modelFile = null;
    private String equipList = null;
    private int modelType = 0;
    private String contentPath = null;
    private String bgColor = null;
    private boolean watermark = false;
    private int spin = 0;
    private String screenshotInfo = null;
    private Color color;
    private int hairColor;
    private int hairType;
    private int faceType;
    private int skinColor;
    private int facialHairType;
    private int facialHairColor;
    private int globalTime = 0;
    private long lastTime = 0L;
    private long delta = 0L;
    private int mesh = 0;
    private ArrayList downloads;
    private TextRenderer mTextRender;
    private int canvasWidth;
    private int canvasHeight;
    private float xDelta;
    private float yDelta;
    private GLCanvas canvas;
    
    private ArrayList<IAnimChangedListener> animListener;

    public ModelViewer(String contentPath, String model, String bgColor, String equipList, int modelType, boolean watermark, String spin, String hc, String hs, String fa, String sk, String fh, String fc, String animation, boolean caching)
    {
        this.caching = caching;
        this.contentPath = contentPath;
        this.newModel = model;
        this.bgColor = bgColor;
        this.equipList = equipList;
        this.modelType = modelType;
        this.watermark = watermark;
        this.spin = spin != null ? Integer.parseInt(spin) : 0;
        this.hairColor = hc != null ? Integer.parseInt(hc) : 0;
        this.hairType = hs != null ? Integer.parseInt(hs) : 0;
        this.faceType = fa != null ? Integer.parseInt(fa) : 0;
        this.skinColor = sk != null ? Integer.parseInt(sk) : 0;
        this.facialHairType = fh != null ? Integer.parseInt(fh) : 0;
        this.facialHairColor = fc != null ? Integer.parseInt(fc) : 0;
        this.animation = animation;
        if (modelType == 128)
        {
            this.modelType = 16;
            this.newModel = "bloodelfmale";
        }
    }

    public ModelViewer(String contentPath, String model, String bgColor, String equipList, int modelType, boolean watermark, String spin, String hc, String hs, String fa, String sk, String fh, String fc, String animation)
    {
        this(contentPath, model, bgColor, equipList, modelType, watermark, spin, hc, hs, fa, sk, fh, fc, animation, true);
    }

    public ModelViewer()
    {
        this.logoTexture = null;
        this.logoTextureStream = null;
        this.mouseRButtonDown = false;
        this.mouseLButtonDown = false;
        this.mouseFromLButton = false;
        this.color = null;
        this.xDelta = 0.0F;
        this.yDelta = 0.0F;
    }

    public void init(Dimension d)
    {
        try
        {
            System.getProperty("java.library.path");
            Util.loadNativeLibarys();
        } catch (Exception ex)
        {
        }
        GLProfile glp = GLProfile.get("GL2");
        GLProfile.initSingleton(true);
        GLCapabilities caps = new GLCapabilities(glp);
        caps.setSampleBuffers(true);
        caps.setNumSamples(4);
        caps.setHardwareAccelerated(true);
        caps.setDoubleBuffered(true);
        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);
        canvas.setSize(d);
        this.animator = new FPSAnimator(canvas, 60);
        downloads = new ArrayList(8);
        mTextRender = new TextRenderer(new Font("SansSerif", 1, 16));
        start();
    }

    @Override
    public void init(GLAutoDrawable drawable)
    {
        GL2 gl = drawable.getGL().getGL2();
        gl.setSwapInterval(1);
        gl.glTexEnvi(8960, 8704, 34160);
        gl.glShadeModel(7425);
        gl.glHint(3154, 4354);
        gl.glEnable(3553);
        gl.glBlendFunc(770, 771);
        gl.glEnable(3008);
        gl.glAlphaFunc(518, 0.7F);
        gl.glDepthFunc(515);
        gl.glEnable(2929);
        gl.glLightModeli(2898, 1);
        float ambient[] =
        {
            0.35F, 0.35F, 0.35F, 1.0F
        };
        float diffuse[] =
        {
            1.0F, 1.0F, 1.0F, 1.0F
        };
        float diffuse1[] =
        {
            0.65F, 0.65F, 0.65F, 1.0F
        };
        float specular[] =
        {
            0.9F, 0.9F, 0.9F, 1.0F
        };
        gl.glLightfv(16384, 4608, ambient, 0);
        gl.glLightfv(16384, 4609, diffuse, 0);
        gl.glLightfv(16384, 4610, specular, 0);
        gl.glLightfv(16385, 4608, ambient, 0);
        gl.glLightfv(16385, 4609, diffuse1, 0);
        gl.glLightfv(16385, 4610, specular, 0);
        gl.glLightfv(16386, 4608, ambient, 0);
        gl.glLightfv(16386, 4609, diffuse1, 0);
        gl.glLightfv(16386, 4610, specular, 0);
        float pos1[] =
        {
            5F, -5F, 5F, 1.0F
        };
        float pos2[] =
        {
            5F, 5F, 5F, 0.0F
        };
        float pos3[] =
        {
            -5F, 5F, 5F, 1.0F
        };
        gl.glLightfv(16384, 4611, pos1, 0);
        gl.glLightfv(16385, 4611, pos2, 0);
        gl.glLightfv(16386, 4611, pos3, 0);
        gl.glEnable(16384);
        gl.glEnable(16385);
        gl.glEnable(16386);
        if (gl.isExtensionAvailable("GL_ARB_multisample"))
        {
            gl.glEnable(32925);
        }
        if (drawable instanceof GLCanvas)
        {
            GLCanvas canvas = (GLCanvas) drawable;
            canvas.addMouseListener(this);
            canvas.addMouseMotionListener(this);
            canvas.addMouseWheelListener(this);
        }
        try
        {
            color = Color.decode(bgColor);
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
            color = Color.black;
        }
        gl.glClearColor((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, 1.0F);
        FileLoader fl;
        fl = new FileLoader(1, this, this, "badge.png", this);
        fl.start();
        cam = new Camera();
        model = new Model(this);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
    {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        canvasWidth = width;
        canvasHeight = height;
        gl.glMatrixMode(5889);
        gl.glLoadIdentity();
        glu.gluPerspective(45F, (float) width / (float) height, 0.1F, 1000F);
        gl.glMatrixMode(5888);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0F, 0.0F, -3F);
    }

    @Override
    public void display(GLAutoDrawable drawable)
    {
        if (screenshotInfo != null)
        {
            try
            {
                BufferedImage image = Screenshot.readToBufferedImage(canvasWidth, canvasHeight);
                int id = 0;
                try
                {
                    id = Integer.parseInt(postImage(image, screenshotInfo));
                } catch (Exception e)
                {
                    System.out.println((new StringBuilder("Post and parse exception: ")).append(e).toString());
                    e.printStackTrace();
                }
            } catch (Exception e)
            {
                System.out.println((new StringBuilder("Screenshot exception: ")).append(e).toString());
                e.printStackTrace();
            }
            screenshotInfo = null;
        }
        GL2 gl = drawable.getGL().getGL2();
        if ((drawable instanceof GLJPanel) && !((GLJPanel) drawable).isOpaque() && ((GLJPanel) drawable).shouldPreserveColorBufferIfTranslucent())
        {
            gl.glClear(256);
        } else
        {
            gl.glClear(16640);
        }
        if (lastTime == 0L)
        {
            lastTime = System.currentTimeMillis();
        }
        delta = System.currentTimeMillis() - lastTime;
        globalTime = (int) ((long) globalTime + delta);
        lastTime = System.currentTimeMillis();
        if (watermark && logoTexture != null)
        {
            gl.glMatrixMode(5889);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glOrtho(0.0D, canvasWidth, canvasHeight, 0.0D, -1D, 1.0D);
            gl.glMatrixMode(5888);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glEnable(3553);
            gl.glDisable(3008);
            gl.glDisable(2929);
            gl.glEnable(3042);
            gl.glDepthMask(false);
            logoTexture.enable(gl);
            logoTexture.bind(gl);
            int width = logoTexture.getWidth();
            int width2 = width / 2;
            int height = logoTexture.getHeight();
            int height2 = height / 2;
            int horizRepeat = canvasWidth / width + 1;
            int vertRepeat = canvasHeight / height + 1;
            int offset = width2;
            gl.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
            gl.glBegin(7);
            for (int y = 0; y < vertRepeat; y += 2)
            {
                if (y % 4 > 0)
                {
                    offset = -width2;
                } else
                {
                    offset = width2;
                }
                for (int x = 0; x < horizRepeat; x += 2)
                {
                    gl.glTexCoord2i(0, 0);
                    gl.glVertex2i(x * width + offset, y * height + height2);
                    gl.glTexCoord2i(0, 1);
                    gl.glVertex2i(x * width + offset, y * height + height + height2);
                    gl.glTexCoord2i(1, 1);
                    gl.glVertex2i(x * width + width + offset, y * height + height + height2);
                    gl.glTexCoord2i(1, 0);
                    gl.glVertex2i(x * width + width + offset, y * height + height2);
                }

            }

            gl.glEnd();
            gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            logoTexture.disable(gl);
            gl.glDepthMask(true);
            gl.glEnable(2929);
            gl.glPopMatrix();
            gl.glMatrixMode(5889);
            gl.glPopMatrix();
            gl.glMatrixMode(5888);
        }
        if (newModel != null)
        {
            setModel();
        }
        xDelta = currMouseX - prevMouseX;
        yDelta = currMouseY - prevMouseY;
        prevMouseX = currMouseX;
        prevMouseY = currMouseY;
        float thetaX = 6.283185F * (xDelta / (float) canvasWidth);
        float thetaY = 6.283185F * (yDelta / (float) canvasHeight);
        if (mouseRButtonDown)
        {
            cam.translate(xDelta, -yDelta, 0.0F);
            xDelta = yDelta = 0.0F;
        } else if (mouseFromLButton)
        {
            if ((double) Math.abs(xDelta) > 0.10000000000000001D || Math.abs(yDelta) > 0.0F)
            {
                cam.rotate(-thetaX, -thetaY);
                xDelta /= 1.1499999999999999D;
                yDelta = 0.0F;
            } else
            {
                if ((double) Math.abs(xDelta) < 0.10000000000000001D)
                {
                    xDelta = 0.0F;
                    mouseFromLButton = mouseLButtonDown;
                }
                yDelta = 0.0F;
            }
        }
        if (mouseLButtonDown)
        {
            xDelta = yDelta = 0.0F;
        }
        if (spin > 0)
        {
            cam.rotate(-6.283185F * ((float) spin / 10000F), 0.0F);
        }
        cam.look(gl);
        try
        {
            if (logoTextureStream != null)
            {
                logoTexture = TextureIO.newTexture(logoTextureStream, true, "png");
                logoTextureStream = null;
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }
        gl.glEnable(2896);
        gl.glEnable(3042);
        gl.glBlendFunc(770, 771);
        if (model != null)
        {

            if (model.modelDownloadFailed == true)
            {
                if (modelType == 1)
                {
                    modelType = 2;
                    setModel();
                } else if (modelType == 2)
                {
                    modelType = 4;
                    setModel();
                } else
                {
                    mTextRender.beginRendering(drawable.getWidth(), drawable.getHeight());
                    mTextRender.setColor(255, 255, 255, 1.0F);
                    mTextRender.draw("Download of Model failed. (Maybe this model doesnt exist ?)", 10, 10);
                    mTextRender.endRendering();
                }
            }

            model.render(gl, cam, true);
        }
        gl.glDisable(2896);
        gl.glBlendFunc(770, 771);
        gl.glMatrixMode(5889);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(0.0D, canvasWidth, canvasHeight, 0.0D, -1D, 1.0D);
        gl.glMatrixMode(5888);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glEnable(3553);
        gl.glDisable(3008);
        gl.glEnable(3042);
        if (downloads.size() > 0)
        {
            int totalSize = 0;
            int totalProgress = 0;
            for (int i = 0; i < downloads.size(); i++)
            {
                FileDownloadEntry entry = (FileDownloadEntry) downloads.get(i);
                totalProgress += entry.progress;
                totalSize += entry.length;
            }

            int colAvg = (this.color.getRed() + this.color.getGreen() + this.color.getBlue()) / 3;
            float color = 0.0F;
            if (colAvg < 96)
            {
                color = 1.0F;
            }
            mTextRender.beginRendering(drawable.getWidth(), drawable.getHeight());
            mTextRender.setColor(color, color, color, 1.0F);
            mTextRender.draw((new StringBuilder("Loading: ")).append(Math.floor(((double) totalProgress / (double) totalSize) * 100D)).append("%").toString(), 10, 10);
            mTextRender.endRendering();
        }
        if (!watermark && logoTexture != null)
        {
            logoTexture.enable(gl);
            logoTexture.bind(gl);
            gl.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
            gl.glBegin(7);
            gl.glTexCoord2i(0, 0);
            gl.glVertex2i(canvasWidth - logoTexture.getWidth(), canvasHeight - logoTexture.getHeight());
            gl.glTexCoord2i(0, 1);
            gl.glVertex2i(canvasWidth - logoTexture.getWidth(), canvasHeight);
            gl.glTexCoord2i(1, 1);
            gl.glVertex2i(canvasWidth, canvasHeight);
            gl.glTexCoord2i(1, 0);
            gl.glVertex2i(canvasWidth, canvasHeight - logoTexture.getHeight());
            gl.glEnd();
            gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            logoTexture.disable(gl);
        }
        gl.glPopMatrix();
        gl.glMatrixMode(5889);
        gl.glPopMatrix();
        gl.glMatrixMode(5888);
    }

    @Override
    public void dispose(GLAutoDrawable glautodrawable)
    {
    }

    public void displayChanged(GLAutoDrawable glautodrawable, boolean flag, boolean flag1)
    {
    }

    @Override
    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    @Override
    public void mouseExited(MouseEvent mouseevent)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        currMouseX = prevMouseX = e.getX();
        currMouseY = prevMouseY = e.getY();
        if ((e.getModifiers() & 0x10) != 0)
        {
            mouseLButtonDown = true;
            mouseFromLButton = true;
        }
        if ((e.getModifiers() & 4) != 0)
        {
            mouseRButtonDown = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if ((e.getModifiers() & 0x10) != 0)
        {
            mouseLButtonDown = false;
        }
        if ((e.getModifiers() & 4) != 0)
        {
            mouseRButtonDown = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        cam.zoom(e.getWheelRotation());
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        currMouseX = e.getX();
        currMouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent mouseevent)
    {
    }

    @Override
    public void RequestComplete(Object origin, int id, InputStream stream, String path)
    {
        if (id == 1)
        {
            logoTextureStream = stream;
        }
    }

    @Override
    public void RequestFailed(Object obj, int i, String s)
    {
    }

    /*
     * Adds equip to the model
     * @param _equip The ids of the items seperator by a comma ? (needs more research)
     */
    public void AddStuff(String _equip)
    {
        if (model == null)
        {
            System.out.println("Adding items while model is null");
        }
        if (_equip != null)
        {
            String equip[] = _equip.split(",");
            if (equip != null && equip.length % 2 == 0)
            {
                boolean mainhandEquipped = false;
                for (int i = 0; i < equip.length; i += 2)
                {
                    int slot = Integer.parseInt(equip[i]);
                    if (slot == 13 || slot == 17 || slot == 21)
                    {
                        if (mainhandEquipped)
                        {
                            equip[i] = "23";
                        } else
                        {
                            mainhandEquipped = true;
                        }
                    }
                    AddArmor(equip[i], equip[i + 1]);
                }

            }
        }
    }

    /*
     * Adds a armor item to the model
     * @param slotStr The id of the slot where the item should be placed
     * @param idStr The id of the item
     */
    public void AddArmor(String slotStr, String idStr)
    {
        if (model != null)
        {
            int slot = Integer.parseInt(slotStr);
            if (slot == 26)
            {
                slot = 25;
            }
            FileLoader fl = null;
            switch (slot)
            {
                case 1: // '\001'
                case 3: // '\003'
                    model.AttachModel((new StringBuilder("armor/")).append(slot).append("/").append(idStr).toString(), slot);
                    break;

                case 4: // '\004'
                case 5: // '\005'
                case 6: // '\006'
                case 7: // '\007'
                case 8: // '\b'
                case 9: // '\t'
                case 10: // '\n'
                case 16: // '\020'
                case 19: // '\023'
                case 20: // '\024'
                    fl = new FileLoader(1001, model, (new StringBuilder("models/armor/")).append(slot).append("/").append(idStr).append(".sis").toString().toLowerCase(), this);
                    fl.start();
                    break;

                case 13: // '\r'
                case 14: // '\016'
                case 15: // '\017'
                case 17: // '\021'
                case 21: // '\025'
                case 22: // '\026'
                case 23: // '\027'
                case 25: // '\031'
                case 26: // '\032'
                    model.AttachModel((new StringBuilder("item/")).append(idStr).toString(), slot);
                    break;

                case 2: // '\002'
                case 11: // '\013'
                case 12: // '\f'
                case 18: // '\022'
                case 24: // '\030'
                default:
                    System.out.println((new StringBuilder("Unhandled slot: ")).append(slot).toString());
                    break;
            }
        }
    }

    /*
     * Downloads everything which is attached to the model and the model itself
     */
    protected void setModel()
    {
        try
        {
            String modelPath = "models/";
            switch (modelType)
            {
                case 1: // '\001'
                    modelPath = (new StringBuilder(String.valueOf(modelPath))).append("item/").append(newModel).append(".mum").toString();
                    break;

                case 2: // '\002'
                    modelPath = (new StringBuilder(String.valueOf(modelPath))).append("armor/1/").append(newModel).append("_1_0.mum").toString();
                    break;

                case 4: // '\004'
                    modelPath = (new StringBuilder(String.valueOf(modelPath))).append("armor/3/").append(newModel).append("_2.mum").toString();
                    break;

                case 8: // '\b'
                    modelPath = (new StringBuilder(String.valueOf(modelPath))).append("npc/").append(newModel).append(".mum").toString();
                    break;

                case 16: // '\020'
                    modelPath = (new StringBuilder(String.valueOf(modelPath))).append("char/").append(newModel).append(".mo2").toString();
                    break;

                case 32: // ' '
                    modelPath = (new StringBuilder(String.valueOf(modelPath))).append("npc/").append(newModel).append(".sis").toString();
                    break;

                case 64: // '@'
                    modelPath = (new StringBuilder(String.valueOf(modelPath))).append("obj/").append(newModel).append(".mum").toString();
                    break;

                default:
                    System.out.println((new StringBuilder("Unhandled model type: ")).append(modelType).toString());
                    break;
            }
            FileLoader loader = null;
            if (modelType == 32)
            {
                loader = new FileLoader(1002, model, modelPath.toLowerCase(), this);
            } else if (modelType == 16)
            {
                loader = new FileLoader(10000, model, modelPath.toLowerCase(), this);
            } else
            {
                loader = new FileLoader(1004, model, modelPath.toLowerCase(), this);
            }
            modelFile = newModel;
            boolean loaded = model.mLoaded;
            loader.start();
            if (!loaded)
            {
                AddStuff(equipList);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        newModel = null;
    }

    /*
     * This method is used to upload a screenshot to wowhead
     * @param cookie The cookie which is needed to login
     * @return the server response as a string
     */
    public String postImage(BufferedImage image, String cookie)
    {
        HttpURLConnection uc = null;
        String lineEnd = "\r\n";
        String hyphens = "--";
        String boundary = "*****------*****";
        String serverResponse = "";
        String urlString = "http://anadept.dev.wowhead.com/screenshot=upload";
        try
        {
            URL url = new URL(urlString);
            uc = (HttpURLConnection) url.openConnection();
            uc.setDoInput(true);
            uc.setDoOutput(true);
            uc.setUseCaches(false);
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Connection", "Keep-Alive");
            uc.setRequestProperty("Content-Type", (new StringBuilder("multipart/form-data;boundary=")).append(boundary).toString());
            DataOutputStream os = new DataOutputStream(uc.getOutputStream());
            os.writeBytes((new StringBuilder(String.valueOf(hyphens))).append(boundary).append(lineEnd).toString());
            os.writeBytes((new StringBuilder("Content-Disposition: form-data; name=\"cookie\"")).append(lineEnd).toString());
            os.writeBytes(lineEnd);
            os.writeBytes((new StringBuilder(String.valueOf(cookie))).append(lineEnd).toString());
            os.writeBytes((new StringBuilder(String.valueOf(hyphens))).append(boundary).append(lineEnd).toString());
            os.writeBytes((new StringBuilder("Content-Disposition: form-data; name=\"upload\"; filename=\"screenshot.png\"")).append(lineEnd).toString());
            os.writeBytes((new StringBuilder("Content-Type: image/png")).append(lineEnd).toString());
            os.writeBytes(lineEnd);
            ImageIO.write(image, "png", os);
            os.writeBytes(lineEnd);
            os.writeBytes((new StringBuilder(String.valueOf(hyphens))).append(boundary).append(hyphens).append(lineEnd).toString());
            os.flush();
            os.close();
            BufferedReader is = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String str;
            while ((str = is.readLine()) != null)
            {
                serverResponse = str;
                System.out.println(serverResponse);
            }
            is.close();
        } catch (Exception e)
        {
            System.out.println((new StringBuilder("Exception: ")).append(e).toString());
            e.printStackTrace();
        }
        return serverResponse;
    }

    /*
     * Downloads the content of the passed urlconnection.
     * While downloading the file is added to the downloads list.
     * To get the progress of a file simply get the values of the downloads list
     */
    @Override
    public InputStream downloadWithProgress(URLConnection con)
            throws IOException
    {
        int curMax = 16384;
        byte mainBuf[] = new byte[curMax];
        int amountRead = 0;
        InputStream is = con.getInputStream();
        int count = is.read(mainBuf, 0, curMax);
        FileDownloadEntry entry = new FileDownloadEntry();
        int contentLength = con.getContentLength();
        if (contentLength != -1)
        {
            entry.length = contentLength;
        } else
        {
            entry.length = curMax * 10;
        }
        downloads.add(entry);
        for (; count != -1; count = is.read(mainBuf, amountRead, curMax - amountRead))
        {
            if (amountRead + count == curMax)
            {
                curMax *= 2;
                byte tmp[] = new byte[curMax];
                System.arraycopy(mainBuf, 0, tmp, 0, amountRead + count);
                mainBuf = tmp;
            }
            amountRead += count;
            entry.progress = amountRead;
        }

        downloads.remove(entry);
        InputStream returnStream = new ByteArrayInputStream(mainBuf, 0, amountRead);
        return returnStream;
    }

    public double getDownloadProgressInPercent()
    {
        if (downloads.size() > 0)
        {
            int[] progress = getDownloadProgress();
            int totalProgress = progress[0];
            int totalSize = progress[1];
            double percentage = Math.floor(((double) totalProgress / (double) totalSize) * 100D);
            return percentage;
        }
        return 100;
    }

    public int[] getDownloadProgress()
    {
        if (downloads.size() > 0)
        {
            int totalSize = 0;
            int totalProgress = 0;
            for (int i = 0; i < downloads.size(); i++)
            {
                FileDownloadEntry entry = (FileDownloadEntry) downloads.get(i);
                totalProgress += entry.progress;
                totalSize += entry.length;
            }
            return new int[]
            {
                totalProgress, totalSize
            };
        }
        return new int[]
        {
            0, 0
        };
    }

    @Override
    public File getCacheDir()
    {
        return cacheDir;
    }

    @Override
    public boolean isCachingEnabled()
    {
        return this.caching;
    }

    @Override
    public String getContentPath()
    {
        return this.contentPath;
    }

    @Override
    public Model getModel()
    {
        return this.model;
    }

    @Override
    public Camera getCamera()
    {
        return this.cam;
    }

    @Override
    public int getCanvasWidth()
    {
        return this.canvasWidth;
    }

    @Override
    public int getCanvasHeight()
    {
        return this.canvasHeight;
    }

    @Override
    public int getHairType()
    {
        return this.hairType;
    }

    @Override
    public int getHairColor()
    {
        return this.hairColor;
    }

    @Override
    public int getFaceType()
    {
        return this.faceType;
    }

    @Override
    public int getSkinColor()
    {
        return this.skinColor;
    }

    @Override
    public int getFacialHairType()
    {
        return this.facialHairType;
    }

    @Override
    public int getFacialHairColor()
    {
        return this.facialHairColor;
    }

    @Override
    public int getGlobalTime()
    {
        return this.globalTime;
    }

    @Override
    public int getMesh()
    {
        return this.mesh;
    }

    @Override
    public long getDelta()
    {
        return this.delta;
    }

    public void ClearSlot(String slotStr)
    {
        if (model != null)
        {
            model.ClearSlot(Integer.parseInt(slotStr));
        }
    }

    public void ClearSlots(String slotStr)
    {
        if (model != null)
        {
            String[] slots = slotStr.split(",");
            if (slots == null)
            {
                return;
            }
            for (int i = 0; i < slots.length; i++)
            {
                model.ClearSlot(Integer.parseInt(slots[i]));
            }
        }
    }

    public void ClearAllSlots()
    {
        if (model != null)
        {
            model.ClearAllSlots();
        }
    }

    public void setModel(String type, String mod)
    {
        newModel = mod;
        modelType = Integer.parseInt(type);
        setModel();
    }

    public void resetAnimation()
    {
        if (model != null)
        {
            model.SetAnimation("idle");
        }
    }

    public void setAnimation(String anim)
    {
        if (model != null)
        {
            model.SetAnimation(anim);
        }
    }

    public int getNumAnimations()
    {
        int count = 0;
        if (model != null)
        {
            count = model.GetNumAnimations();
        }
        return count;
    }

    public String getAnimation(int index)
    {
        String anim = "";
        if (model != null)
        {
            anim = model.GetAnimation(index);
        }
        return anim;
    }

    public void setAnimationFrame(int frame)
    {
        if (model != null)
        {
            model.SetAnimationFrame(frame);
        }
    }

    public int getNumAnimationFrames()
    {
        int count = 0;
        if (model != null)
        {
            count = model.GetNumAnimationFrames();
        }
        return count;
    }

    public void setWatermark(int enable)
    {
        watermark = enable == 1;
    }

    public void setFreeze(int enable)
    {
        if (model != null)
        {
            model.mFreeze = (enable == 1);
        }
    }

    public void setSpinSpeed(int speed)
    {
        spin = speed;
    }

    public void takeScreenshot(String cookie)
    {
        screenshotInfo = cookie;
    }

    public void setHairStyle(int hs)
    {
        if (hs == hairType)
        {
            return;
        }
        hairType = hs;
        if (model != null)
        {
            model.RefreshAppearance();
        }
    }

    public void setHairColor(int hc)
    {
        if (hc == hairColor)
        {
            return;
        }
        hairColor = hc;
        if (model != null)
        {
            model.RefreshAppearance();
        }
    }

    public void setFaceType(int fa)
    {
        if (fa == faceType)
        {
            return;
        }
        faceType = fa;
        if (model != null)
        {
            model.RefreshAppearance();
        }
    }

    public void setSkinColor(int sk)
    {
        if (sk == skinColor)
        {
            return;
        }
        skinColor = sk;
        if (model != null)
        {
            model.RefreshAppearance();
        }
    }

    public void setFacialHairStyle(int fh)
    {
        if (fh == facialHairType)
        {
            return;
        }
        facialHairType = fh;
        if (model != null)
        {
            model.RefreshAppearance();
        }
    }

    public void setFacialHairColor(int fc)
    {
        if (fc == facialHairColor)
        {
            return;
        }
        facialHairColor = fc;
        if (model != null)
        {
            model.RefreshAppearance();
        }
    }

    public void setAppearance(int hs, int hc, int fa, int sk, int fh, int fc)
    {
        hairType = hs;
        hairColor = hc;
        faceType = fa;
        skinColor = sk;
        facialHairType = fh;
        facialHairColor = fc;
        if (model != null)
        {
            model.RefreshAppearance();
        }
    }

    public int getHairStyles()
    {
        if ((model != null) && (model.mHairStyles != null))
        {
            return model.mHairStyles.length;
        }
        return 0;
    }

    public int getHairColors()
    {
        if (model != null)
        {
            return model.mNumHairColors;
        }
        return 0;
    }

    public int getFaceTypes()
    {
        if (model != null)
        {
            return model.mNumFaceTypes;
        }
        return 0;
    }

    public int getSkinColors()
    {
        if ((model != null) && (model.mSkinColors != null))
        {
            return model.mSkinColors.length;
        }
        return 0;
    }

    public int getFacialHairStyles()
    {
        if ((model != null) && (model.mFacialHairs != null))
        {
            return model.mFacialHairs.length;
        }
        return 0;
    }

    public int getFacialHairColors()
    {
        if (model != null)
        {
            return model.mNumFacialHairColors;
        }
        return 0;
    }

    public boolean isLoaded()
    {
        if (model != null)
        {
            return model.mLoaded;
        }
        return false;
    }

    public int numMeshes()
    {
        if (model != null)
        {
            return model.mMeshes.length;
        }
        return 0;
    }

    public void toggleMesh(int mesh)
    {
        if (model != null)
        {
            for (int i = 0; i < model.mMeshes.length; i++)
            {
                if (model.mMeshes[i].geoset == mesh)
                {
                    model.mMeshes[i].show = (!model.mMeshes[i].show);
                }
            }
        }
    }

    public void toggleFreeze()
    {
        if (model != null)
        {
            model.mFreeze = (!model.mFreeze);
        }
    }

    public void dumpTranslation()
    {
        if (model != null)
        {
            model.dumpTranslation();
        }
    }

    public void dumpRotation()
    {
        if (model != null)
        {
            model.dumpRotation();
        }
    }

    public void dumpScale()
    {
        if (model != null)
        {
            model.dumpScale();
        }
    }

    public void start()
    {
        this.animator.start();
    }

    public void stop()
    {
        if (this.animator != null)
        {
            this.animator.stop();
        }
    }

    public String getNewModel()
    {
        return this.newModel;
    }

    public int getModelType()
    {
        return this.modelType;
    }

    public void clearModel()
    {
        this.model = new Model(this);
    }
    
    public GLCanvas getCanvas(){
        return this.canvas;
    }
    
    public void addAnimationChangedListener(IAnimChangedListener lis){
        if(this.animListener == null){
            this.animListener = new ArrayList<IAnimChangedListener>();
        }
        this.animListener.add(lis);
    }
    
    public void removeAnimationChangedListener(IAnimChangedListener lis){
        if(this.animListener == null)return;
        this.animListener.remove(lis);
    }

    @Override
    public void animationsChanged()
    {
        if(this.animListener == null)return;
        for(IAnimChangedListener lis : this.animListener){
            lis.animationsChanged();
        }
    }
}
