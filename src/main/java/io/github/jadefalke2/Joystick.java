package io.github.jadefalke2;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;

public class Joystick extends JPanel {

	private final int outputMax;
	private final int thumbDiameter;
	private final int thumbRadius;
	private final int panelWidth;
	private final int BORDER_THICKNESS = 2;

	private final Point thumbPos = new Point();
	protected SwingPropertyChangeSupport propertySupporter = new SwingPropertyChangeSupport(this);


	/**
	 * @param output_max  The maximum value to scale output to. If this value was
	 *                    5 and the joystick thumb was dragged to the top-left corner, the output
	 *                    would be (-5,5)
	 * @param panel_width how big the JPanel will be. The sizes of the joystick's
	 *                    visual components are proportional to this value
	 */

	public Joystick(int output_max, int panel_width) {

		assert output_max > 0;
		assert panel_width > 0;

		outputMax = output_max;
		panelWidth = panel_width;
		thumbDiameter = panel_width / 15;
		thumbRadius = thumbDiameter / 2;

		MouseAdapter mouseAdapter = new MouseAdapter() {

			private void repaintAndTriggerListeners() {
				SwingUtilities.getRoot(Joystick.this).repaint();
				propertySupporter.firePropertyChange(null, null, getOutputPos());
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY());
					repaintAndTriggerListeners();
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					updateThumbPos(e.getX(), e.getY());
					repaintAndTriggerListeners();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					repaintAndTriggerListeners();
				}
			}
		};

		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		setPreferredSize(new java.awt.Dimension(panel_width, panel_width));
		setOpaque(false);

		centerThumbPad();
	}

	private void centerThumbPad() {
		thumbPos.x = panelWidth / 2;
		thumbPos.y = panelWidth / 2;
	}

	/**
	 * update both thumbPos
	 *
	 * @param mouseX the x position of cursor that has clicked in the joystick panel
	 * @param mouseY the y position of cursor that has clicked in the joystick panel
	 * @return
	 */

	private void updateThumbPos(int mouseX, int mouseY) {
		// if the cursor is clicked out of bounds, we'll modify the position
		// to be the closest point where we can draw the thumb pad completely
		if (mouseX < thumbRadius)
			mouseX = thumbRadius;
		else if (mouseX > panelWidth - thumbRadius)
			mouseX = panelWidth - thumbRadius;

		if (mouseY < thumbRadius)
			mouseY = thumbRadius;
		else if (mouseY > panelWidth - thumbRadius)
			mouseY = panelWidth - thumbRadius;

		thumbPos.x = mouseX;
		thumbPos.y = mouseY;
	}

	/**
	 * @return the scaled position of the joystick thumb pad
	 */

	public Point getOutputPos() {
		Point result = new Point();
		result.x = outputMax * (thumbPos.x - panelWidth / 2) / (panelWidth / 2 - thumbDiameter / 2);
		result.y = -outputMax * (thumbPos.y - panelWidth / 2) / (panelWidth / 2 - thumbDiameter / 2);
		return result;
	}

	public void setThumbPos (Point scaled){
		thumbPos.x = (scaled.x/outputMax) * (panelWidth / 2 - thumbDiameter / 2) + (panelWidth / 2);
		thumbPos.y = (scaled.y/-outputMax) * (panelWidth / 2 - thumbDiameter / 2) + (panelWidth / 2);
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		//joystick background border
		g.setColor(Color.BLACK);
		g.fillOval(thumbRadius, thumbRadius, panelWidth - thumbDiameter, panelWidth - thumbDiameter);

		//joystick background color
		g.setColor(Color.GRAY);
		g.fillOval(thumbRadius + BORDER_THICKNESS, thumbRadius + BORDER_THICKNESS, panelWidth - thumbDiameter - BORDER_THICKNESS * 2, panelWidth - thumbDiameter - BORDER_THICKNESS * 2);

		//Middle lines
		g.setColor(Color.black);
		g.drawLine(panelWidth / 2, thumbRadius + BORDER_THICKNESS, panelWidth / 2, panelWidth - thumbRadius - BORDER_THICKNESS);
		g.drawLine(thumbRadius + BORDER_THICKNESS, panelWidth / 2, panelWidth - thumbRadius - BORDER_THICKNESS, panelWidth / 2);

		//thumb pad border
		g.setColor(Color.BLACK);
		g.fillOval(thumbPos.x - thumbRadius - BORDER_THICKNESS, thumbPos.y - thumbRadius - BORDER_THICKNESS, thumbRadius * 2 + BORDER_THICKNESS * 2, thumbRadius * 2 + BORDER_THICKNESS * 2);

		//thumb pad color
		g.setColor(Color.RED);
		g.fillOval(thumbPos.x - thumbRadius, thumbPos.y - thumbRadius, thumbRadius * 2, thumbRadius * 2);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupporter.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupporter.removePropertyChangeListener(listener);
	}
}
