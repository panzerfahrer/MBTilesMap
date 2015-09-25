/**
 *
 */
package de.slowpoke.android.mbtiles;

/**
 * Defines boundaries for a tile map.
 */
public class MBTilesBounds {

    /**
     * Left/West boundary
     */
    public final float left;

    /**
     * Right/East boundary
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
        this.bottom = bottom;
        this.right = right;
        this.top = top;
    }

    /**
     * Create new bounds from an array of floats
     *
     * @param bounds an array of floats with the length of <code>4</code> with the following ordering <code>[left, bottom, right,
     *               top]</code>
     */
    public MBTilesBounds(float[] bounds) {
        if (bounds != null && bounds.length == 4) {
            this.left = bounds[0];
            this.bottom = bounds[1];
            this.right = bounds[2];
            this.top = bounds[3];
        } else {
            this.left = -180.0f;
            this.right = -85.0f;
            this.top = 180.0f;
            this.bottom = 85.0f;
        }
    }

    /**
     * Create new bounds from a String
     *
     * @param string <code>left,bottom,right,top</code> e.g. <code>-180.0,-85.0,180.0,85.0</code>
     */
    public MBTilesBounds(String string) throws NumberFormatException {

        String[] splitted = string.split(",");
        float[] floats = new float[]{-180.0f, -85.0f, 180.0f, 85.0f};
        if (splitted.length == 4) {
            floats[0] = Float.parseFloat(splitted[0]);
            floats[1] = Float.parseFloat(splitted[1]);
            floats[2] = Float.parseFloat(splitted[2]);
            floats[3] = Float.parseFloat(splitted[3]);
        }

        this.left = floats[0];
        this.bottom = floats[1];
        this.right = floats[2];
        this.top = floats[3];
    }

    /**
     * @return [left, bottom, right, top]. Full earth: -180.0,-85.0,180.0,85.0
     */
    public float[] toFloatArray() {
        return new float[]{left, bottom, right, top};
    }

    /**
     * Checks if the tile with the supplied coordinates is within the bounds (and thus is actually available)
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public boolean isInBounds(final int x, final int y, final int z) {
        if (left == -180.0f && bottom == -85.0f && right == 180.0f && top == 85.0f) {
            return true;
        } else if (left == 0.0f && bottom == 0.0f && right == 0.0f && top == 0.0f) {
            return false;
        } else {
            final double zoom = Math.pow(2, z);
            final double longitudeSpan = 360.0 / zoom;
            final double mercatorMax = 180 - (y / zoom) * 360;
            final double mercatorMin = 180 - ((y + 1) / zoom) * 360;

            final double latMin = toLatitude(mercatorMin);
            final double latMax = toLatitude(mercatorMax);
            final double lonMin = -180.0 + x * longitudeSpan;
            final double lonMax = lonMin + longitudeSpan;

            return latMin >= left && latMax <= right && lonMin >= top && lonMax <= bottom;
        }
    }

    static double toLatitude(double mercator) {
        double radians = Math.atan(Math.exp(Math.toRadians(mercator)));
        return Math.toDegrees(2 * radians) - 90;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MBTilesBounds that = (MBTilesBounds) o;

        if (Float.compare(that.left, left) != 0) return false;
        if (Float.compare(that.right, right) != 0) return false;
        if (Float.compare(that.top, top) != 0) return false;
        return Float.compare(that.bottom, bottom) == 0;

    }

    @Override
    public int hashCode() {
        int result = (left != +0.0f ? Float.floatToIntBits(left) : 0);
        result = 31 * result + (right != +0.0f ? Float.floatToIntBits(right) : 0);
        result = 31 * result + (top != +0.0f ? Float.floatToIntBits(top) : 0);
        result = 31 * result + (bottom != +0.0f ? Float.floatToIntBits(bottom) : 0);
        return result;
    }

    /**
     * -180.0,-85.0,180.0,85.0
     */
    @Override
    public String toString() {
        return left + "," + bottom + "," + right + "," + top;
    }

}
