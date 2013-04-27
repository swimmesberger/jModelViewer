package org.fseek.thedeath.modelview.views;

import org.fseek.thedeath.modelview.interfaces.IAnimChangedListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.fseek.thedeath.modelview.Model;
import org.fseek.thedeath.modelview.ModelViewer;
import org.fseek.thedeath.modelview.util.Util;

public class ModelViewerFrame extends JFrame implements MouseListener, MouseMotionListener, MouseWheelListener, IAnimChangedListener
{

    private ModelViewer viewer;
    private JComboBox animationBox = new JComboBox();

    public ModelViewerFrame()
    {
        this.viewer = new ModelViewer();
    }

    public ModelViewerFrame(String contentPath, String model, String bgColor, String equipList, int modelType, boolean watermark, String spin, String hc, String hs, String fa, String sk, String fh, String fc, String animation, boolean caching)
    {
        this.viewer = new ModelViewer(contentPath, model, bgColor, equipList, modelType, watermark, spin, hc, hs, fa, sk, fh, fc, animation, caching);
    }

    public ModelViewerFrame(String contentPath, String model, String bgColor, String equipList, int modelType, boolean watermark, String spin, String hc, String hs, String fa, String sk, String fh, String fc, String animation)
    {
        this.viewer = new ModelViewer(contentPath, model, bgColor, equipList, modelType, watermark, spin, hc, hs, fa, sk, fh, fc, animation);
    }

    public void init()
    {
        this.setTitle("WoWHead Modelviewer");
        setLayout(new BorderLayout());
        viewer.init(this.getSize());
        add(viewer.getCanvas(), "Center");
        setBackground(new Color(1579032));
        initComponents();
        viewer.addAnimationChangedListener(this);
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new FrameKeyDispatcher());
    }

    private void initComponents()
    {
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                viewer.stop();
            }
        });
        JPanel menu = new JPanel();
        this.animationBox.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent e)
            {
                Object selectedItem = animationBox.getSelectedItem();
                if (selectedItem == null)
                {
                    return;
                }
                String toString = selectedItem.toString();
                viewer.setAnimation(toString);
            }
        });
        menu.setLayout(new BorderLayout(10, 0));
        menu.add(this.animationBox, "West");
        JPanel selectDisplayId = new JPanel();
        JLabel displayID = new JLabel("DisplayID:");
        final JTextField field = new JTextField();
        field.setText(viewer.getNewModel());
        JButton change = new JButton("Change");
        selectDisplayId.setLayout(new BorderLayout(10, 0));
        selectDisplayId.add(displayID, "West");
        selectDisplayId.add(field, "Center");
        selectDisplayId.add(change, "East");
        change.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String text = field.getText();
                viewer.clearModel();
                //hard code the set modeltype (in the default case its 8)
                viewer.setModel(viewer.getModelType() + "", text);
            }
        });

        menu.add(selectDisplayId, "Center");

        getContentPane().add(menu, "South");
    }

    @Override
    public void mouseEntered(MouseEvent mouseevent)
    {
        viewer.mouseEntered(mouseevent);
    }

    @Override
    public void mouseExited(MouseEvent mouseevent)
    {
        viewer.mouseExited(mouseevent);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        viewer.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        viewer.mouseReleased(e);
    }

    @Override
    public void mouseClicked(MouseEvent mouseevent)
    {
        viewer.mouseReleased(mouseevent);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        viewer.mouseWheelMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        viewer.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent mouseevent)
    {
        viewer.mouseDragged(mouseevent);
    }
    
    private boolean fullScreen = false;
    /*
     * Fake fullscreen -> just maximises the window. We should take a look at exclusive fullscreen mode.
     */
    private void toggleFullScreen()
    {
        this.setExtendedState(this.fullScreen? JFrame.NORMAL  : JFrame.MAXIMIZED_BOTH);
        this.fullScreen = !this.fullScreen;
    }

    public static void main(String[] args)
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                Util.showModelViewer(null);
            }
        });
    }
    private HashSet<String> animations = new HashSet<String>();
    /*
     * Add animations threaded and also make sure each animation is added only once (HashSet) because in the model some animations are saved multiple times
     */

    @Override
    public void animationsChanged()
    {
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                animations.clear();
                Model m = viewer.getModel();
                for (int i = 0; i < m.GetNumAnimations(); i++)
                {
                    animations.add(m.GetAnimation(i));
                }
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        animationBox.removeAllItems();
                        for (String s : animations)
                        {
                            animationBox.addItem(s);
                        }
                        animationBox.setSelectedItem("Stand");
                    }
                });
            }
        };
        t.setName("Add Animations thread");
        t.start();
    }
    
    private class FrameKeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch(e.getKeyCode()){
                    case KeyEvent.VK_F11:
                        ModelViewerFrame.this.toggleFullScreen();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        ModelViewerFrame.this.toggleFullScreen();
                        break;
                }
            }
            return false;
        }
    }
}