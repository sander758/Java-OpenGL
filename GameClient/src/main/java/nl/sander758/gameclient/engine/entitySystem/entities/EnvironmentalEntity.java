package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.Model;
import org.joml.Vector3f;

public abstract class EnvironmentalEntity extends Entity {

    public EnvironmentalEntity(Model model, Vector3f location) {
        super(model);
        Vector3f entityLocation = getLocation();
        entityLocation.x = location.x;
        entityLocation.y = location.y;
        entityLocation.z = location.z;
//        updateTransformationMatrix();
    }
}
