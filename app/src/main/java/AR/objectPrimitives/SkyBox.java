package AR.objectPrimitives;

import android.graphics.Bitmap;
import AR.Shared;
import AR.Utils;
import AR.core.Object3dContainer;
import AR.vos.Color4;

public class SkyBox extends Object3dContainer {
    private float size;
    private float halfSize;
    private int quality;
    private Color4 color;
    private Rectangle[] faces;

    public enum Face {
        North,
        East,
        South,
        West,
        Up,
        Down,
        All
    }

    public SkyBox(float size, int quality) {
        super(0, 0);
        this.size = size;
        this.halfSize = size *.5f;
        this.quality = quality;
        build();
    }

    private void build() {
        color = new Color4();
        faces = new Rectangle[6];
        Rectangle north = new Rectangle(size, size, quality, quality, color);
        Rectangle east = new Rectangle(size, size, quality, quality, color);
        Rectangle south = new Rectangle(size, size, quality, quality, color);
        Rectangle west = new Rectangle(size, size, quality, quality, color);
        Rectangle up = new Rectangle(size, size, quality, quality, color);
        Rectangle down = new Rectangle(size, size, quality, quality, color);

        north.position().z = halfSize;
        north.lightingEnabled(false);

        east.rotation().y = -90;
        east.position().x = -halfSize;
        east.lightingEnabled(false);

        south.rotation().y = 180;
        south.position().z = -halfSize;
        south.lightingEnabled(false);

        west.rotation().y = 90;
        west.position().x = halfSize;
        west.lightingEnabled(false);

        up.rotation().x = -90;
        up.position().y = halfSize;
        up.lightingEnabled(false);

        down.rotation().x = 90;
        down.position().y = -halfSize;
        down.lightingEnabled(false);

        faces[Face.North.ordinal()] = north;
        faces[Face.East.ordinal()] = east;
        faces[Face.South.ordinal()] = south;
        faces[Face.West.ordinal()] = west;
        faces[Face.Up.ordinal()] = up;
        faces[Face.Down.ordinal()] = down;

        addChild(north);
        addChild(east);
        addChild(south);
        addChild(west);
        addChild(up);
        addChild(down);
    }

    public void addTexture(Face face, String resourceId, String id) {
        Bitmap bitmap = Utils.makeBitmapFromResourceId(resourceId);
        Shared.textureManager().addTextureId(bitmap, id, false);
        bitmap.recycle();
        addTexture(face, bitmap, id);
    }

    public void addTexture(Face face, Bitmap bitmap, String id) {
        if(face == Face.All)
        {
            for(int i=0; i<6; i++)
            {
                faces[i].textures().addById(id);
            }
        }
        else
        {
            faces[face.ordinal()].textures().addById(id);
        }
    }
}
