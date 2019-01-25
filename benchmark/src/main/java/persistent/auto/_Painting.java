package persistent.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.property.EntityProperty;
import org.apache.cayenne.exp.property.NumericProperty;
import org.apache.cayenne.exp.property.PropertyFactory;
import org.apache.cayenne.exp.property.StringProperty;

import persistent.Artist;

/**
 * Class _Painting was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _Painting extends BaseDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final NumericProperty<Integer> ARTIST_ID = PropertyFactory.createNumeric("artistId", Integer.class);
    public static final NumericProperty<Integer> GALLERY_ID = PropertyFactory.createNumeric("galleryId", Integer.class);
    public static final NumericProperty<Integer> ID = PropertyFactory.createNumeric("id", Integer.class);
    public static final StringProperty<String> NAME = PropertyFactory.createString("name", String.class);
    public static final EntityProperty<Artist> ARTIST = PropertyFactory.createEntity("artist", Artist.class);

    protected Integer artistId;
    protected Integer galleryId;
    protected Integer id;
    protected String name;

    protected Object artist;

    public void setArtistId(Integer artistId) {
        beforePropertyWrite("artistId", this.artistId, artistId);
        this.artistId = artistId;
    }

    public Integer getArtistId() {
        beforePropertyRead("artistId");
        return this.artistId;
    }

    public void setGalleryId(Integer galleryId) {
        beforePropertyWrite("galleryId", this.galleryId, galleryId);
        this.galleryId = galleryId;
    }

    public Integer getGalleryId() {
        beforePropertyRead("galleryId");
        return this.galleryId;
    }

    public void setId(Integer id) {
        beforePropertyWrite("id", this.id, id);
        this.id = id;
    }

    public Integer getId() {
        beforePropertyRead("id");
        return this.id;
    }

    public void setName(String name) {
        beforePropertyWrite("name", this.name, name);
        this.name = name;
    }

    public String getName() {
        beforePropertyRead("name");
        return this.name;
    }

    public void setArtist(Artist artist) {
        setToOneTarget("artist", artist, true);
    }

    public Artist getArtist() {
        return (Artist)readProperty("artist");
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "artistId":
                return this.artistId;
            case "galleryId":
                return this.galleryId;
            case "id":
                return this.id;
            case "name":
                return this.name;
            case "artist":
                return this.artist;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "artistId":
                this.artistId = (Integer)val;
                break;
            case "galleryId":
                this.galleryId = (Integer)val;
                break;
            case "id":
                this.id = (Integer)val;
                break;
            case "name":
                this.name = (String)val;
                break;
            case "artist":
                this.artist = val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.artistId);
        out.writeObject(this.galleryId);
        out.writeObject(this.id);
        out.writeObject(this.name);
        out.writeObject(this.artist);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.artistId = (Integer)in.readObject();
        this.galleryId = (Integer)in.readObject();
        this.id = (Integer)in.readObject();
        this.name = (String)in.readObject();
        this.artist = in.readObject();
    }

}
