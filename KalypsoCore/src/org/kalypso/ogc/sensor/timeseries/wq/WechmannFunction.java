package org.kalypso.ogc.sensor.timeseries.wq;

/**
 * The Wechmann Function. Performs conversion from W to Q and from Q to W
 * according to the Wechmann parameters.
 * <p>
 * The Wechmann Function is defined as follows:
 * 
 * <pre>
 * Q = exp( ln( K1 ) + ln( W - W1 ) * K2 )
 * 
 * having:
 * 
 * Q = computed runoff (in m�/s)
 * W = current waterlevel (in cm at the Gauge) oder auf Deutsch (cm am Pegel)
 * 
 * and
 * 
 * the Wechmann Parameters: ln(K1), W1,  K2
 * </pre>
 * 
 * @author schlienger
 */
public class WechmannFunction
{
  private WechmannFunction()
  {
    // not to be instanciated
  }

  /**
   * @see WechmannFunction#computeQ(double, double, double, double)
   */
  public static final double computeQ( final WechmannParams wp, final double W )
  {
    return computeQ( wp.getLNK1(), W, wp.getW1(), wp.getK2() );
  }
  
  /**
   * Computes the Q using the following function:
   * <pre>
   * Q = exp( ln( K1 ) + ln( W - W1 ) * K2 )
   * </pre>
   */
  public static final double computeQ( final double LNK1, final double W, final double W1, final double K2 )
  {
    return Math.exp( LNK1 + Math.log( W - W1 ) * K2 );
  }

  /**
   * @see WechmannFunction#computeW(double, double, double, double)
   */
  public static final double computeW( final WechmannParams wp, final double Q )
  {
    return computeW( wp.getW1(), Q, wp.getLNK1(), wp.getK2() );
  }
  
  /**
   * Computes the W using the following function:
   * <pre>
   *               ln( Q ) - ln( K1 )
   * W = W1 + exp( ------------------ )
   *                       K2
   * </pre>
   */
  public static final double computeW( final double W1, final double Q, final double LNK1, final double K2 )
  {
    return W1 + Math.exp( ( Math.log( Q ) - LNK1 ) / K2 );
  }
}