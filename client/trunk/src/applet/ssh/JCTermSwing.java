/* -*-mode:java; c-basic-offset:2; -*- */
/* JCTerm
 * Copyright (C) 2002,2007 ymnk, JCraft,Inc.
 *  
 * Written by: ymnk<ymnk@jcaft.com>
 *   
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 * 
 * You should have received a copy of the GNU Library General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package applet.ssh;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;

public class JCTermSwing extends JPanel implements KeyListener, /* Runnable, */
Term {
	OutputStream out;
	InputStream in;
	Emulator emulator = null;

	Connection connection = null;

	private BufferedImage img;
	private BufferedImage background;
	private Graphics2D cursor_graphics;
	private Graphics2D graphics;
	private JTextField textField;
	private java.awt.Color defaultbground = Color.black;
	private java.awt.Color defaultfground = Color.white;
	private java.awt.Color bground = Color.black;
	private java.awt.Color fground = Color.white;
	private java.awt.Component term_area = null;
	private java.awt.Font font;

	private boolean bold = false;
	private boolean underline = false;
	private boolean reverse = false;

	private int term_width = 80;
	private int term_height = 24;

	private int descent = 0;

	private int x = 0;
	private int y = 0;

	private int char_width;
	private int char_height;

	// private int line_space=0;
	private int line_space = -2;
	private int compression = 0;

	private boolean antialiasing = true;

	private Splash splash = null;

	private final Object[] colors = { Color.black, Color.red, Color.green,
			Color.yellow, Color.blue, Color.magenta, Color.cyan, Color.white };

	public JCTermSwing() {

		enableEvents(AWTEvent.KEY_EVENT_MASK);
		addKeyListener(this);

		setFont("Monospaced-14");

		background = new BufferedImage(char_width, char_height,
				BufferedImage.TYPE_INT_RGB);
		{
			Graphics2D foog = (Graphics2D) (background.getGraphics());
			foog.setColor(getBackGround());
			foog.fillRect(0, 0, char_width, char_height);
			foog.dispose();
		}

		setSize(getTermWidth(), getTermHeight());
		if (splash != null)
			//splash.draw(img, getTermWidth(), getTermHeight());
			splash.draw(textField, getTermWidth(), getTermHeight());
		else
			clear();

		term_area = this;

		setPreferredSize(new Dimension(getTermWidth(), getTermHeight()));

		setSize(getTermWidth(), getTermHeight());
		setFocusable(true);
		enableInputMethods(true);

		setFocusTraversalKeysEnabled(false);
		// setOpaque(true);
	}

	private void setFont(String fname) {
		font = java.awt.Font.decode(fname);
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) (img.getGraphics());
		JTextField jtf = new JTextField();
		jtf.setFont(font);
		graphics.setFont(font);
		{
			FontMetrics fo = jtf.getFontMetrics(font);
			
			//FontMetrics fo = graphics.getFontMetrics();
			descent = fo.getDescent();
			/*
			 * System.out.println(fo.getDescent());
			 * System.out.println(fo.getAscent());
			 * System.out.println(fo.getLeading());
			 * System.out.println(fo.getHeight());
			 * System.out.println(fo.getMaxAscent());
			 * System.out.println(fo.getMaxDescent());
			 * System.out.println(fo.getMaxDecent());
			 * System.out.println(fo.getMaxAdvance());
			 */
			char_width = (int) (fo.charWidth((char) '@'));
			char_height = (int) (fo.getHeight()) + (line_space * 2);
			descent += line_space;
		}
		img.flush();
		jtf.setVisible(true);
		graphics.dispose();
	}

	public void setSize(int w, int h) {

		super.setSize(w, h);
		BufferedImage imgOrg = img;
		if (graphics != null)
			graphics.dispose();

		int column = w / getCharWidth();
		int row = h / getCharHeight();
		term_width = column;
		term_height = row;

		if (emulator != null)
			emulator.reset();

		img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		graphics = (Graphics2D) (img.getGraphics());
		graphics.setFont(font);

		clear_area(0, 0, w, h);

		if (imgOrg != null) {
			Shape clip = graphics.getClip();
			graphics.setClip(0, 0, getTermWidth(), getTermHeight());
			graphics.drawImage(imgOrg, 0, 0, term_area);
			graphics.setClip(clip);
		}

		if (cursor_graphics != null)
			cursor_graphics.dispose();

		cursor_graphics = (Graphics2D) (img.getGraphics());
		cursor_graphics.setColor(getForeGround());
		cursor_graphics.setXORMode(getBackGround());

		setAntiAliasing(antialiasing);

		if (connection != null) {
			connection.requestResize(this);
		}

		if (imgOrg != null) {
			imgOrg.flush();
			imgOrg = null;
		}
	}

	public void start(Connection connection) {
		this.connection = connection;
		in = connection.getInputStream();
		out = connection.getOutputStream();
		emulator = new EmulatorVT100(this, in);
		emulator.reset();
		emulator.start();

		if (splash != null)
			splash.draw(img, getTermWidth(), getTermHeight());
		else
			clear();
		redraw(0, 0, getTermWidth(), getTermHeight());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			g.drawImage(img, 0, 0, term_area);
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	public void processKeyEvent(KeyEvent e) {
		// System.out.println(e);
		int id = e.getID();
		if (id == KeyEvent.KEY_PRESSED) {
			keyPressed(e);
		} else if (id == KeyEvent.KEY_RELEASED) {
			/* keyReleased(e); */
		} else if (id == KeyEvent.KEY_TYPED) {
			keyTyped(e);/* keyTyped(e); */
		}
		e.consume(); // ??
	}

	byte[] obuffer = new byte[3];

	public void keyPressed(KeyEvent e){
    int keycode=e.getKeyCode();
    byte[] code=null;
    switch(keycode){

      case KeyEvent.VK_CONTROL:
      case KeyEvent.VK_SHIFT:
      case KeyEvent.VK_ALT:
      case KeyEvent.VK_CAPS_LOCK:
        return;
      case KeyEvent.VK_ENTER:
        code=emulator.getCodeENTER();
        break;
      case KeyEvent.VK_UP:
        code=emulator.getCodeUP();
        break;
      case KeyEvent.VK_DOWN:
        code=emulator.getCodeDOWN();
        break;
      case KeyEvent.VK_RIGHT:
        code=emulator.getCodeRIGHT();
        break;
      case KeyEvent.VK_LEFT:
        code=emulator.getCodeLEFT();
        break;
      case KeyEvent.VK_F1:
        code=emulator.getCodeF1();
        break;
      case KeyEvent.VK_F2:
        code=emulator.getCodeF2();
        break;
      case KeyEvent.VK_F3:
        code=emulator.getCodeF3();
        break;
      case KeyEvent.VK_F4:
        code=emulator.getCodeF4();
        break;
      case KeyEvent.VK_F5:
        code=emulator.getCodeF5();
        break;
      case KeyEvent.VK_F6:
        code=emulator.getCodeF6();
        break;
      case KeyEvent.VK_F7:
        code=emulator.getCodeF7();
        break;
      case KeyEvent.VK_F8:
        code=emulator.getCodeF8();
        break;
      case KeyEvent.VK_F9:
        code=emulator.getCodeF9();
        break;
      case KeyEvent.VK_F10:
        code=emulator.getCodeF10();
        break;
      case KeyEvent.VK_TAB:
        code=emulator.getCodeTAB();
        break;
    }
    if(code!=null){
      try{
        out.write(code, 0, code.length);
        out.flush();
      }
      catch(Exception ee){
      }
      return;
    }

    char keychar=e.getKeyChar();
    if((keychar&0xff00)==0){
      obuffer[0]=(byte)(e.getKeyChar());
      try{
        out.write(obuffer, 0, 1);
        out.flush();
      }
      catch(Exception ee){
      }
    }
  }

	public void keyTyped(KeyEvent e) {
		char keychar = e.getKeyChar();
		if ((keychar & 0xff00) != 0) {
			char[] foo = new char[1];
			foo[0] = keychar;
			try {
				byte[] goo = new String(foo).getBytes("EUC-JP");
				out.write(goo, 0, goo.length);
				out.flush();
			} catch (Exception eee) {
			}
		}
	}

	public int getTermWidth() {
		return char_width * term_width;
	}

	public int getTermHeight() {
		return char_height * term_height;
	}

	public int getCharWidth() {
		return char_width;
	}

	public int getCharHeight() {
		return char_height;
	}

	public int getColumnCount() {
		return term_width;
	}

	public int getRowCount() {
		return term_height;
	}

	public void clear() {
		graphics.setColor(getBackGround());
		graphics.fillRect(0, 0, char_width * term_width, char_height
				* term_height);
		graphics.setColor(getForeGround());
	}

	public void setCursor(int x, int y) {
		// System.out.println("setCursor: "+x+","+y);
		this.x = x;
		this.y = y;
	}

	public void draw_cursor() {
		cursor_graphics.fillRect(x, y - char_height, char_width, char_height);
		repaint(x, y - char_height, char_width, char_height);
	}

	public void redraw(int x, int y, int width, int height) {
		repaint(x, y, width, height);
	}

	public void clear_area(int x1, int y1, int x2, int y2) {
		// System.out.println("clear_area: "+x1+" "+y1+" "+x2+" "+y2);
		graphics.setColor(getBackGround());
		graphics.fillRect(x1, y1, x2 - x1, y2 - y1);
		graphics.setColor(getForeGround());
	}

	public void scroll_area(int x, int y, int w, int h, int dx, int dy) {
		// System.out.println("scroll_area: "+x+" "+y+" "+w+" "+h+" "+dx+" "+dy);
		graphics.copyArea(x, y, w, h, dx, dy);
		repaint(x + dx, y + dy, w, h);
	}

	public void drawBytes(byte[] buf, int s, int len, int x, int y) {
		// clear_area(x, y, x+len*char_width, y+char_height);
		// graphics.setColor(getForeGround());

		// System.out.println("drawString: "+x+","+y+" "+len+" "+new String(buf,
		// s, len));

		graphics.drawBytes(buf, s, len, x, y - descent);
		if (bold)
			graphics.drawBytes(buf, s, len, x + 1, y - descent);

		if (underline) {
			graphics.drawLine(x, y - 1, x + len * char_width, y - 1);
		}

	}

	public void drawString(String str, int x, int y) {
		// clear_area(x, y, x+str.length()*char_width, y+char_height);
		// graphics.setColor(getForeGround());
		graphics.drawString(str, x, y - descent);
		if (bold)
			graphics.drawString(str, x + 1, y - descent);

		if (underline) {
			graphics.drawLine(x, y - 1, x + str.length() * char_width, y - 1);
		}

	}

	public void beep() {
		Toolkit.getDefaultToolkit().beep();
	}

	/** Ignores key released events. */
	public void keyReleased(KeyEvent event) {
	}

	// public void keyPressed(KeyEvent event){}

	public void setSplash(Splash foo) {
		this.splash = foo;
	}

	public void setLineSpace(int foo) {
		this.line_space = foo;
	}

	public boolean getAntiAliasing() {
		return antialiasing;
	}

	public void setAntiAliasing(boolean foo) {
		if (graphics == null)
			return;
		antialiasing = foo;
		java.lang.Object mode = foo ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
				: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING, mode);
		graphics.setRenderingHints(hints);
	}

	public void setCompression(int compression) {
		if (compression < 0 || 9 < compression)
			return;
		this.compression = compression;
	}

	private java.awt.Color toColor(Object o) {
		if (o instanceof String) {
			return java.awt.Color.getColor((String) o);
		}
		if (o instanceof java.awt.Color) {
			return (java.awt.Color) o;
		}
		return Color.white;
	}

	public void setDefaultForeGround(Object f) {
		defaultfground = toColor(f);
	}

	public void setDefaultBackGround(Object f) {
		defaultbground = toColor(f);
	}

	public void setForeGround(Object f) {
		fground = toColor(f);
		graphics.setColor(getForeGround());
	}

	public void setBackGround(Object b) {
		bground = toColor(b);
		Graphics2D foog = (Graphics2D) (background.getGraphics());
		foog.setColor(getBackGround());
		foog.fillRect(0, 0, char_width, char_height);
		foog.dispose();
	}

	private java.awt.Color getForeGround() {
		if (reverse)
			return bground;
		return fground;
	}

	private java.awt.Color getBackGround() {
		if (reverse)
			return fground;
		return bground;
	}

	public Object getColor(int index) {
		if (colors == null || index < 0 || colors.length <= index)
			return null;
		return colors[index];
	}

	public void setBold() {
		bold = true;
	}

	public void setUnderline() {
		underline = true;
	}

	public void setReverse() {
		reverse = true;
		if (graphics != null)
			graphics.setColor(getForeGround());
	}

	public void resetAllAttributes() {
		bold = false;
		underline = false;
		reverse = false;
		bground = defaultbground;
		fground = defaultfground;
		if (graphics != null)
			graphics.setColor(getForeGround());
	}
}
