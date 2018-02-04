package nl.sander758.gameclient.engine.entitySystem.entities;

import nl.sander758.gameclient.engine.loader.Model;
import org.joml.Vector3f;

public abstract class EnvironmentalEntity extends StaticEntity {

    public EnvironmentalEntity(Model model, Vector3f location) {
        super(model, location);
    }
}
