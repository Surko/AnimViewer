
package components;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * floating point version of JSlider.
 * replaces the original integer get/set Minimum/Maximum/Value methods with the
 * floating point versions: get/set FloatMinimum/FloatMaximum/FloatValue.
 * it is an error to call the original versions or other methods related to them.
 * note that unlike in the base class, values set via setFloatValue(val) are allowed
 * to be outside the min/max range values. in those cases the slider thumb will clamp
 * to the appropriate slider end (just like in the base class) but the out-of-range
 * value will still be retrivable with getFloatValue().
 * 
 * @author Melinda Green
 * @author Don Hatch
 */
public class JSliderDouble extends JSlider {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1519037863876327879L;
	private double curFloat, minFloat, maxFloat;
	private boolean isLogScale;

	/**
	 * constructs a JSliderDouble using a given number of slider positions.
	 * @param orientation - Scrollbar.VERTICAL or Scrollbar.HORIZONTAL.
	 * @param cur - real valued initial value.
	 * @param vis - same as in Scrollbar base class.
	 * @param min - real valued range minimum.
	 * @param max - real valued range maximum.
	 * @param resolution - number of descrete slider positions.
	 * @param log - log scale if true, linear otherwise.
	 */
	public JSliderDouble(int orientation, double cur, double min, double max, int res, boolean log) {
		super(orientation, 0, res, 0);
		isLogScale = log;
		setAll(min, max, cur);

		addChangeListener(new SliderListener());
		//addAdjustmentListener(new AdjustmentListener() {
		//    public void adjustmentValueChanged(AdjustmentEvent ae) {
		//        int ival = JSliderDouble.super.getValue();
		//        int min = JSliderDouble.super.getMinimum();
		//        int max = JSliderDouble.super.getMaximum();
		//        double dval = transformRange(false,      min,      max,  ival,
		//                                     isLogScale, minFloat, maxFloat);
		//        System.out.println("getting: ival="+ival+" -> dval="+dval);
		//        setFloatValue(dval);
		//    }
		//});
	}
	/**
	 * uses default scale (linear).
	 */
	//public JSliderDouble(int orientation, double cur, double min, double max, int res) {
	//     this(orientation, cur, min, max, res, false);
	//}
	/**
	 * uses default visible(20) and resolution(1000).
	 */
	//public JSliderDouble(int orientation, double cur, double min, double max, boolean log) {
	//    this(orientation, cur, min, max, DEFAULT_RANGE, log);
	//}
	/**
	 * uses default visible(20), resolution(1000), and scale (linear).
	 */
	// public JSliderDouble(int orientation, double cur, double min, double max) {
	//    this(orientation, cur, min, max, DEFAULT_RANGE, false);
	//}
	class SliderListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider)e.getSource();

			if(source.getValueIsAdjusting()){
				//.....
			}

			int ival = JSliderDouble.super.getValue();
			int min = JSliderDouble.super.getMinimum();
			int max = JSliderDouble.super.getMaximum();
			double dval = transformRange(false,      min,      max,  ival,
					isLogScale, minFloat, maxFloat);
			//System.out.println("getting: ival="+ival+" -> dval="+dval);
			setFloatValue(dval);

		}
	}

	/**
	 * returns the closest integer in the range of the actual int extents of the base Scrollbar.
	 */
	private int rangeValue(double dval) {
		dval = clamp(dval, minFloat, maxFloat);
		//int vis = super.getVisibleAmount(),
		int min = super.getMinimum();
		int max = super.getMaximum();
		//int ival = (int)Math.round(transformRange(isLogScale, minFloat, maxFloat, dval, false, min, max-vis));
		int ival = (int)Math.round(transformRange(isLogScale, minFloat, maxFloat, dval, false, min, max));
		//System.out.println("setting: dval="+dval+" -> ival="+ival);
		return ival;
	}

	public double getFloatMinimum() {
		return minFloat;
	}
	public double getFloatMaximum() {
		return maxFloat;
	}
	public double getFloatValue() {
		int ival = JSliderDouble.super.getValue();
		int min = JSliderDouble.super.getMinimum();
		int max = JSliderDouble.super.getMaximum();
		double dval = transformRange(false,      min,      max,  ival,
				isLogScale, minFloat, maxFloat);
		//System.out.println("getting: ival="+ival+" -> dval="+dval);
		setFloatValue(dval);

		return curFloat;
	}

	public void setFloatMinimum(double newmin) {
		setAll(newmin, maxFloat, getFloatValue());
	}
	public void setFloatMaximum(double newmax) {
		setAll(minFloat, newmax, getFloatValue());
	}
	public void setFloatValue(double newcur) {
		// update the model
		curFloat = newcur;
		// update the view
		super.setValue(rangeValue(newcur));
	}

	private void setAll(double newmin, double newmax, double newcur) {
		minFloat = newmin;
		maxFloat = newmax;
		setFloatValue(newcur);
	}

	private static double clamp(double x, double a, double b)
	{
		return x <= a ? a :
			x >= b ? b : x;
	}
	// linear interpolation
	private static double lerp(double a, double b, double t)
	{
		return a + (b-a) * t;
	}
	// geometric interpolation
	private static double gerp(double a, double b, double t)
	{
		return a * Math.pow(b/a, t);
	}
	// interpolate between A and B (linearly or geometrically)
	// by the fraction that x is between a and b (linearly or geometrically)
	private static double
	transformRange(boolean isLog, double a, double b, double x,
			boolean IsLog, double A, double B)

	{
		if (isLog)
		{
			a = Math.log(a);
			b = Math.log(b);
			x = Math.log(x);
		}
		double t = (x-a) / (b-a);
		double X = IsLog ? gerp(A,B,t)
				: lerp(A,B,t);
		return X;
	}

	/**
	 * simple test program for FloatSlider class.
	 */
	//    public static void main(String args[]) {
	//        Frame frame = new Frame("FloatSlider example");
	//        final FloatSlider rslider = new FloatSlider(Scrollbar.HORIZONTAL, 100.1f, 5.5f, 50000f, true);
	//        final Label curValue = new Label("FloatSlider value: " + rslider.getFloatValue());
	//        rslider.addAdjustmentListener(new AdjustmentListener() {
	//    public void adjustmentValueChanged(AdjustmentEvent ae) {
	//                curValue.setText("FloatSlider value: " + rslider.getFloatValue());
	//            }
	//        });
	//        Container mainpanel = new Panel();
	//        mainpanel.setLayout(new GridLayout(3, 1));
	//        mainpanel.add(new Label("Range: " + rslider.getFloatMinimum() + " -> " + rslider.getFloatMaximum()));
	//        mainpanel.add(rslider);
	//        mainpanel.add(curValue);
	//        frame.add(mainpanel);
	//        frame.setSize(new Dimension(800, 100));
	//        frame.addWindowListener(new WindowAdapter() {
	//            public void windowClosing(WindowEvent we) {
	//                System.exit(1);
	//            }
	//        });
	//        frame.setVisible(true);
	//    }

}