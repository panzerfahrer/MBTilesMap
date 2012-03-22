/**
 * 
 */
package de.slowpoke.mbtilesmap;

import java.io.Serializable;

/**
 * Defines boundaries for a tile map.
 * 
 * @author Brian
 * 
 */
public class MBTilesBounds implements Serializable {

	private static final long serialVersionUID = 3853149213984773294L;

	/**
	 * Left/West boundaray
	 */
	public final float left;

	/**
	 * Right/East Boundaray
	 */
	public final float right;

	/**
	 * Top/North boundary
	 */
	public final float top;

	/**
	 * Bottom/South boundary
	 */
	public final float bottom;

	/**
	 * Create new bounds.
	 * 
	 * @param left
	 * @param bottom
	 * @param right
	 * @param top
	 */
	public MBTilesBounds(float left, float bottom, float right, float top) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * Create new bounds from an array of floats
	 * 
	 * @param bounds
	 *            an array of floats with the length of <code>4</code> with the
	 *            following ordering <code>[left, bottom, right, top]</code>
	 */
	public MBTilesBounds(float[] bounds) {
		if (bounds != null && bounds.length == 4) {
			this.left = bounds[0];
			this.right = bounds[1];
			this.top = bounds[2];
			this.bottom = bounds[3];
		} else {
			this.left = 0;
			this.right = 0;
			this.top = 0;
			this.bottom = 0;
		}
	}

	/**
	 * Create new bounds from a String
	 * 
	 * @param string
	 *            <code>left,bottom,right,top</code> e.g.
	 *            <code>-180.0,-85,180,85</code>
	 */
	public MBTilesBounds(String string) throws NumberFormatException {

		String[] splitted = string.split(",");
		float[] floats = new float[4];
		if (splitted.length == 4) {
			floats[0] = Float.parseFloat(splitted[0]);
			floats[1] = Float.parseFloat(splitted[1]);
			floats[2] = Float.parseFloat(splitted[2]);
			floats[3] = Float.parseFloat(splitted[3]);
		}

		this.left = floats[0];
		this.right = floats[1];
		this.top = floats[2];
		this.bottom = floats[3];
	}

	/**
	 * @return // [left, bottom, right, top] | Full earth: -180.0,-85,180,85
	 */
	public float[] toFloatArray() {
		return new float[] { left, bottom, right, top };
	}

	/**
	 * Checks if the tile with the supplied coordinates is within the bounds
	 * (and thus is actually available)
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public boolean isInBounds(final int x, final int y, final int z) {
		// TODO
		return true;
	}

	/**
	 * -180.0,-85,180,85
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return left + "," + bottom + "," + right + "," + top;
	}

	@Override
	public boolean equals(Object another) {
		if (another instanceof MBTilesBounds) {
			return this.left == ((MBTilesBounds) another).left && this.bottom == ((MBTilesBounds) another).bottom
			        && this.right == ((MBTilesBounds) another).right && this.top == ((MBTilesBounds) another).top;
		}

		return false;
	}

}
