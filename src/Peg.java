

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Peg {
	private Canvas canvas_;
	private Color color_;
	private Color highlight_;

	public Peg ( double size ) {
		canvas_ = new Canvas(size,size);
		color_ = null;
		highlight_ = null;
		draw();
	}

	public Peg ( Color color, double size ) {
		canvas_ = new Canvas(size,size);
		color_ = color;
		highlight_ = null;
		draw();
	}

	public Canvas getCanvas () {
		return canvas_;
	}

	public Color getColor () {
		return color_;
	}

	public void setColor ( Color color ) {
		color_ = color;
		draw();
	}

	public void highlight ( Color color ) {
		highlight_ = color;
		draw();
	}

	public void unhighlight () {
		highlight_ = null;
		draw();
	}

	void draw () {
		GraphicsContext g = canvas_.getGraphicsContext2D();
		g.clearRect(0,0,canvas_.getWidth(),canvas_.getHeight());

		if ( highlight_ != null ) {
			g.setFill(highlight_);
		} else if ( color_ != null ) {
			g.setFill(color_);
		} else {
			g.setFill(Color.rgb(16,16,16));
		}
		g.setStroke(Color.BLACK);

		if ( color_ == null && highlight_ == null ) {
			g.fillOval(canvas_.getWidth() / 6,canvas_.getHeight() / 6,
			           canvas_.getWidth() * 2 / 3,canvas_.getHeight() * 2 / 3);
		} else {
			g.fillOval(0,0,canvas_.getWidth(),canvas_.getHeight());
			g.strokeOval(0,0,canvas_.getWidth(),canvas_.getHeight());
		}
	}
}
