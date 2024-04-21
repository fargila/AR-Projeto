package resources.arRes;

import AR.core.Object3dContainer;
import AR.animation.AnimationObject3d;
/**
 * Interface for 3D object parsers
 *
 * @author dennis.ippel
 *
 */
public interface IParser {
    /**
     * Start parsing the 3D object
     */
    public void parse();

    /**
     * Returns the parsed object
     *
     * @return
     */
    public Object3dContainer getParsedObject();

    /**
     * Returns the parsed animation object
     *
     * @return
     */
    public AnimationObject3d getParsedAnimationObject();
}
