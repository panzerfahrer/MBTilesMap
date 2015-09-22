/**
 *
 */
package de.slowpoke.mbtilesmap;

/**
 * Available (and implemented) MBTiles versions.
 *
 * @author Brian
 */
public enum MBTilesVersion {

    VERSION_1_0("1.0"),

    VERSION_1_1("1.1");

    public final String versionName;

    private MBTilesVersion(String versionName) {
        this.versionName = versionName;
    }

    /**
     * Get a {@link MBTilesVersion} from string version name.
     *
     * @param versionName such as <code>1.0</code>
     * @return <code>null</code> if the supplied versionName does not match a
     * valid version.
     */
    public static MBTilesVersion fromString(String versionName) {
        for (MBTilesVersion value : values()) {
            if (value.versionName.equals(versionName)) {
                return value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return versionName;
    }

}
