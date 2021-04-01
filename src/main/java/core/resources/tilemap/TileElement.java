package core.resources.tilemap;

import java.util.ArrayList;

public class TileElement {
    public ArrayList<TileProperty> properties = new ArrayList<>();

    public Object getProperty(String name) {
        for(var property : properties) {
            if(property.name.equals(name)) {
                return property.value;
            }
        }
        return null;
    }
}
