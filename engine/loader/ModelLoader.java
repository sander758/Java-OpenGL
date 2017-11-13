package loader;

import entities.EntityManager;
import models.RawModel;
import org.lwjgl.util.vector.Vector3f;
import scene.Scene;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelLoader {

    private static ModelLoader modelLoader = new ModelLoader();

    private HashMap<String, RawModel> models = new HashMap<>();

    private ModelLoader() {}

    public static ModelLoader getModelLoader() {
        return modelLoader;
    }

    public Scene loadScene(Path path) {
        Scene scene = new Scene();
        try {
            if (!Files.exists(path)) {
                throw new IOException("Saves path not found");
            }

            List<String> lines = Files.readAllLines(path);

            for (String line : lines) {
                if (!line.startsWith("e ")) {
                    continue;
                }

                String[] splitLine = line.split(" ");

                RawModel model = ModelLoader.getModelLoader().loadObject(splitLine[1]);

                String[] location = splitLine[2].split("/");
                Vector3f entityLocation = new Vector3f(Float.parseFloat(location[0]), Float.parseFloat(location[1]), Float.parseFloat(location[2]));

                String[] rotation = splitLine[3].split("/");
                float rotX = Float.parseFloat(rotation[0]);
                float rotY = Float.parseFloat(rotation[1]);
                float rotZ = Float.parseFloat(rotation[2]);
                float scale = Float.parseFloat(splitLine[4]);

                scene.addEntity(EntityManager.getManager().create(model, entityLocation, rotX, rotY, rotZ, scale));
            }
        } catch (IOException e) {
            System.out.println("Something went wrong with reading saves");
            e.printStackTrace();
        }
        return scene;
    }

    public RawModel loadObject(String name) throws IOException {
        if (models.containsKey(name)) {
            return models.get(name);
        }

        Path filePath = Paths.get(System.getProperty("user.dir") + "\\res\\save\\" + name + "\\" + name + ".obj");
        if (!Files.exists(filePath)) {
            throw new IOException("Expected object path doesn't exists: " + filePath.toString());
        }
        return loadObjectFile(filePath);
    }

    private Vector3f parseLine(String first, String second, String third) {
        return new Vector3f(Float.parseFloat(first), Float.parseFloat(second), Float.parseFloat(third));
    }

    private Triangle parseFace(String[] currentLine, Material currentMaterial, ArrayList<Integer> materialIndices, IntHolder indicesCount, IntHolder distinctVertexCounter) {
        // Create a new triangle with a certain material.
        Triangle triangle = new Triangle(currentMaterial);

        // The length of the current line is expected to be 4
        // (index 0 is the 'f ' part and the others the vertices of the triangle/face
        for (int i = 1; i < currentLine.length; i++) {
            indicesCount.increment(); // increment the indices amount
            int currentIndex = Integer.parseInt(currentLine[i].split("/")[0]);
            int currentNormal = Integer.parseInt(currentLine[i].split("/")[2]);
            switch (i) {
                case 1:
                    triangle.vertex1 = currentIndex;
                    triangle.normal1 = currentNormal;
                    break;
                case 2:
                    triangle.vertex2 = currentIndex;
                    triangle.normal2 = currentNormal;
                    break;
                case 3:
                    triangle.vertex3 = currentIndex;
                    triangle.normal3 = currentNormal;
                    break;
            }
            if (!materialIndices.contains(currentIndex)) {
                materialIndices.add(currentIndex);
                distinctVertexCounter.increment();
            }
        }

        return triangle;
    }

    private RawModel loadObjectFile(Path filePath) throws IOException {
        if (!checkFilePath(filePath)) {
            throw new IOException("Could not find path");
        }

        long startTime = System.currentTimeMillis();

        // the HashMap that contains all the materials with the string as name.
        HashMap<String, Material> materialHashMap = new HashMap<>();
        // Faces are separated by a material so for every material there is a
        HashMap<Material, ArrayList<Integer>> positionIndices = new HashMap<>();
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normalVectors = new ArrayList<>();


        String line;

        System.out.println("Try loading: " + filePath);
        String originDirectory = String.join("", filePath.toAbsolutePath().toString().split(filePath.getFileName().toString()));
        BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset());
        String name = filePath.getFileName().toString().split("\\.obj")[0];

        // loop through each line of the file and check what it is.
        while ((line = reader.readLine()) != null) {
            String[] currentLine = line.split(" ");
            if (line.startsWith("mtllib ")) {
                this.materialFileReader(Paths.get(originDirectory + currentLine[1]), materialHashMap);
            } else if (line.startsWith("v ")) {
                vertices.add(parseLine(currentLine[1], currentLine[2], currentLine[3]));
            } else if (line.startsWith("vn ")) {
                normalVectors.add(parseLine(currentLine[1], currentLine[2], currentLine[3]));
            } else if (line.startsWith("usemtl ") || line.startsWith("f ")) {
                // stop this loop when it reaches materials
                break;
            }
        }

        // Loop through all materials and add a list for each material.
        // Each int in that list (index/indices) corresponds to a certain vertex.
        for (String materialName : materialHashMap.keySet()) {
            positionIndices.put(materialHashMap.get(materialName), new ArrayList<>());
        }


        // A amount of all the indices of all triangles/faces.
        IntHolder indicesCount = new IntHolder();

        // The sum of distinct vertices per material
        IntHolder distinctVertexCounter = new IntHolder();
        // All the faces/triangles
        List<Triangle> triangles = new ArrayList<>();

        Material currentMaterial = null;
        do {
            if (line == null || (!line.startsWith("f ") && !line.startsWith("usemtl "))) {
                continue;
            }

            String[] currentLine = line.split(" ");
            if (line.startsWith("usemtl ")) {
                currentMaterial = materialHashMap.get(currentLine[1]);
                continue;
            }

            if (line.startsWith("f ") && currentMaterial != null) {
                // parse the face line and make a triangle object
                triangles.add(parseFace(currentLine, currentMaterial, positionIndices.get(currentMaterial), indicesCount, distinctVertexCounter));
            }
        } while ((line = reader.readLine()) != null);


        // Array of all positions that have a different color.
        // So a position can occur twice but that is because one has a different color.
        float[] positions = new float[distinctVertexCounter.getValue() * 3];

        // The color attached to a position.
        float[] colors = new float[distinctVertexCounter.getValue() * 3];

        // The normal that is attached to a position.
        float[] normals = new float[distinctVertexCounter.getValue() * 3];

        // Array of all indices with the length of all vertices of each face.
        int[] indices = new int[indicesCount.getValue()];


        // A map with per material a list of all vertices with their corresponding index to that vertex-color combo.
        HashMap<Material, HashMap<Vector3f, Integer>> materialVertexIndices = new HashMap<>();

        // The index where a certain vertex-color combo is found.
        int vertexPointer = 0;

        // Loop per material. "Green", "Snow" etc
        for (Material indicesMaterial : positionIndices.keySet()) {

            // A list where each integer is a vertexPointer and the vector a vertex.
            HashMap<Vector3f, Integer> vertexMap = new HashMap<>();

            // Loop per index/'vertex id' of a material
            for (int index : positionIndices.get(indicesMaterial)) {
                // Index is the number of the vertex that currently being handled.
                // This loop sets a specific vertex-color combo in the positions array and puts
                // the index of that vertex-color combo in a HashMap for a specific material.

                vertexMap.put(vertices.get(index - 1), vertexPointer);

                // Set the X coordinate of the vertex.
                positions[vertexPointer * 3] = vertices.get(index - 1).x;
                // Set the Y coordinate of the vertex.
                positions[vertexPointer * 3 + 1] = vertices.get(index - 1).y;
                // Set the Z coordinate of the vertex.
                positions[vertexPointer * 3 + 2] = vertices.get(index - 1).z;

                // Set the R value of the vertex.
                colors[vertexPointer * 3] = indicesMaterial.getDiffuseColor().x;
                // Set the G value of the vertex.
                colors[vertexPointer * 3 + 1] = indicesMaterial.getDiffuseColor().y;
                // Set the B value of the vertex.
                colors[vertexPointer * 3 + 2] = indicesMaterial.getDiffuseColor().z;

                vertexPointer++;
            }

            // Put the HashMap with every vertex of that material in the overall HashMap.
            materialVertexIndices.put(indicesMaterial, vertexMap);
        }

        int trianglePointer = 0;
        for (Triangle triangle : triangles) {
            // Get the list of vertex-color combos of the specific material of this triangle.
            HashMap<Vector3f, Integer> vertexMap = materialVertexIndices.get(triangle.material);

            // Get the vertexPointer of the first vertex of the triangle.
            int vertexPositionId1 = vertexMap.get(vertices.get(triangle.vertex1 - 1));
            // Get the vertexPointer of the second vertex of the triangle.
            int vertexPositionId2 = vertexMap.get(vertices.get(triangle.vertex2 - 1));
            // Get the vertexPointer of the third vertex of the triangle.
            int vertexPositionId3 = vertexMap.get(vertices.get(triangle.vertex3 - 1));

            // Set the vertexPointers in the indices array.
            // First vertex of the triangle.
            indices[trianglePointer * 3] = vertexPositionId1;
            // Second vertex of the triangle.
            indices[trianglePointer * 3 + 1] = vertexPositionId2;
            // Third vertex of the triangle.
            indices[trianglePointer * 3 + 2] = vertexPositionId3;

            //*********************************************************************************************************/
            // Get the normal of the first vertex and set the x, y, z coords.
            normals[vertexPositionId1 * 3] = normalVectors.get(triangle.normal1 - 1).x;
            normals[vertexPositionId1 * 3 + 1] = normalVectors.get(triangle.normal1 - 1).y;
            normals[vertexPositionId1 * 3 + 2] = normalVectors.get(triangle.normal1 - 1).z;

            // Get the normal of the second vertex and set the x, y, z coords.
            normals[vertexPositionId2 * 3] = normalVectors.get(triangle.normal2 - 1).x;
            normals[vertexPositionId2 * 3 + 1] = normalVectors.get(triangle.normal2 - 1).y;
            normals[vertexPositionId2 * 3 + 2] = normalVectors.get(triangle.normal2 - 1).z;

            // Get the normal of the third vertex and set the x, y, z coords.
            normals[vertexPositionId3 * 3] = normalVectors.get(triangle.normal3 - 1).x;
            normals[vertexPositionId3 * 3 + 1] = normalVectors.get(triangle.normal3 - 1).y;
            normals[vertexPositionId3 * 3 + 2] = normalVectors.get(triangle.normal3 - 1).z;
            //*********************************************************************************************************/


            //*********************************************************************************************************/
            // Calculate average normal of the triangle and set every vertex normal to that triangle normal.
            // However this won't work if you have triangles with the same material because they could have a different
            // triangle normals and one would overwrite another with as result that there are certain triangles
            // attached to each other with the same color.
//            Vector3f vertexNormal1 = normalVectors.get(triangle.normal1 - 1);
//            Vector3f vertexNormal2 = normalVectors.get(triangle.normal2 - 1);
//            Vector3f vertexNormal3 = normalVectors.get(triangle.normal3 - 1);
//
//            float avrX = (vertexNormal1.x + vertexNormal2.x + vertexNormal3.x) / 3;
//            float avrY = (vertexNormal1.y + vertexNormal2.y + vertexNormal3.y) / 3;
//            float avrZ = (vertexNormal1.z + vertexNormal2.z + vertexNormal3.z) / 3;
//
//            Vector3f currentNormal = new Vector3f(avrX, avrY, avrZ);
//
//            normals[vertexPositionId1 * 3] = currentNormal.x;
//            normals[vertexPositionId1 * 3 + 1] = currentNormal.y;
//            normals[vertexPositionId1 * 3 + 2] = currentNormal.z;
//
//            normals[vertexPositionId2 * 3] = currentNormal.x;
//            normals[vertexPositionId2 * 3 + 1] = currentNormal.y;
//            normals[vertexPositionId2 * 3 + 2] = currentNormal.z;
//
//            normals[vertexPositionId3 * 3] = currentNormal.x;
//            normals[vertexPositionId3 * 3 + 1] = currentNormal.y;
//            normals[vertexPositionId3 * 3 + 2] = currentNormal.z;
            //*********************************************************************************************************/





            trianglePointer++;
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Model loaded in: " + duration + " millis");


        // The numbers are indices to the vertices and normals
        // f vertex/texture/normal vertex/texture/normal vertex/texture/normal
        // f 118   /       /163    130   /       /163    276   /       /163


        GLLoader loader = GLLoader.getLoader();
        RawModel model = loader.loadToVao(positions, colors, normals, indices, name);
        models.put(model.getName(), model);
        return model;
    }

    private boolean checkFilePath(Path filePath) {
        if (Files.isDirectory(filePath)) {
            return false;
        }
        String fileName = filePath.getFileName().toString();
        if (!fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).equals("obj")) {
            return false;
        }
        return true;
    }

    private void materialFileReader(Path file, HashMap<String, Material> materialHashMap) {
        String line;
        StringBuilder fileContent = new StringBuilder();
        try {
            BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset());
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        String fileContentString = fileContent.toString();
        // Split the file content at every new material.
        String[] materials = fileContentString.split("newmtl ");

        // Start at 1 because the first part of the file is just some info and not a material.
        for (int i = 1; i < materials.length; i++) {
            String[] materialLines = materials[i].split("\n");

            // Set the default color to black.
            Vector3f color = new Vector3f(0, 0, 0);
            for (String materialLine : materialLines) {
                if (materialLine.startsWith("Kd ")) {
                    // Split the line and set the rgb values of the color.
                    String[] diffuseColor = materialLine.split(" ");
                    color.setX(Float.parseFloat(diffuseColor[1]));
                    color.setY(Float.parseFloat(diffuseColor[2]));
                    color.setZ(Float.parseFloat(diffuseColor[3]));
                }
            }
            String materialName = materialLines[0];
            // Add the new material to the HashMap
            materialHashMap.put(materialName, new Material(materialName, color));
        }
    }

    private class IntHolder {
        private int value = 0;

        public int getValue() {
            return value;
        }

        public void increment() {
            value++;
        }
    }

    private class Material {
        // The name of the material
        private String name;

        // The value of the diffuse color (basically just the color)
        // Kd value in the .mtl file
        private Vector3f diffuseColor;

        // Constructor
        Material(String name, Vector3f diffuseColor) {
            this.name = name;
            this.diffuseColor = diffuseColor;
        }

        // Getter for diffuse color
        Vector3f getDiffuseColor() {
            return this.diffuseColor;
        }
    }

    private class Triangle {
        int vertex1;    // first vertex
        int vertex2;    // second vertex
        int vertex3;    // third vertex
        int normal1;    // normal of first vertex
        int normal2;    // normal of second vertex
        int normal3;    // normal of third vertex

        // The material this vertex is using
        private Material material;

        Triangle (Material material) {
            this.material = material;
        }
    }
}
