package loader;

import entities.Entity;
import entities.EntityManager;
import models.ModelType;
import models.RawModel;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import scene.Scene;
import entities.Terrain;
import utils.Maths;
import water.WaterTile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SceneLoader {

    /**
     *  Load a scene.
     *
     * @param scenePath the path to the scene file.
     * @return a new scene.
     */
    public Scene loadScene(Path scenePath) {
        Scene scene = new Scene();
        try {

            if (!Files.exists(scenePath)) {
                throw new IOException("Saves path not found");
            }

            List<String> lines = Files.readAllLines(scenePath);

            for (String line : lines) {
                if (line.startsWith("e ")) {
                    scene.addEntity(createEntity(line));
                } else if (line.startsWith("t ")) {
                    scene.addTerrain(createTerrain(line));
                } else if (line.startsWith("w ")) {
                    scene.addWaterTile(createWaterTile(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return scene;
    }

    /**
     * Read a line from the scene file and return an entity.
     *
     * @param line the line from the scene file.
     * @return an entity
     * @throws IOException thrown if something went wrong.
     */
    private Entity createEntity(String line) throws IOException {
        String[] splitLine = line.split(" ");
        ObjectData data = loadObjectData(parsePath(splitLine[1]));

        RawModel model = GLLoader.getLoader().loadToVao(data.positions, data.colors, data.normals, data.indices, splitLine[1]);

        String[] position = splitLine[2].split("/");
        Vector3f entityPosition = parseLine(position[0], position[1], position[2]);

        String[] rotation = splitLine[3].split("/");
        Vector3f entityRotation = parseLine(rotation[0], rotation[1], rotation[2]);

        float scale = Float.parseFloat(splitLine[4]);

        int entityId = EntityManager.getManager().create();
        return new Entity(entityId, model, entityPosition, entityRotation, scale);
    }

    /**
     * Read a line from the scene file and return a terrain entity.
     *
     * @param line the line from the scene file.
     * @return terrain
     * @throws IOException thrown if something went wrong.
     */
    private Terrain createTerrain(String line) throws IOException {
        String[] splitLine = line.split(" ");
        ObjectData data = loadObjectData(parsePath(splitLine[1]));

        String[] position = splitLine[2].split("/");
        Vector3f terrainPosition = parseLine(position[0], position[1], position[2]);

        String[] rotation = splitLine[3].split("/");
        Vector3f terrainRotation = parseLine(rotation[0], rotation[1], rotation[2]);

        Matrix4f transformationMatrix = Maths.createTransformationMatrix(terrainPosition, terrainRotation.x, terrainRotation.y, terrainRotation.z, 1);

        List<Vector3f> vertices = transformVertices(transformationMatrix, data.vertices);

        Vector2f highestSize = new Vector2f(vertices.get(0).x, vertices.get(0).z);
        Vector2f lowestSize = new Vector2f(vertices.get(0).x, vertices.get(0).z);

        for (Vector3f vertex : vertices) {
            if (vertex.x > highestSize.x) {
                highestSize.x = vertex.x;
            }
            if (vertex.z > highestSize.y) {
                highestSize.y = vertex.z;
            }
            if (vertex.x < lowestSize.x) {
                lowestSize.x = vertex.x;
            }
            if (vertex.z < lowestSize.y) {
                lowestSize.y = vertex.z;
            }
        }

        // The x size.
        int xSize = (int) (Math.ceil(highestSize.x) - Math.floor(lowestSize.x));
        // This is the zSize and not y because in 3D it is the z axis.
        int zSize = (int) (Math.ceil(highestSize.y) - Math.floor(lowestSize.y));
        Vector2f size = new Vector2f(xSize, zSize);

        // Vector to put the model to the ( 0, 0) point.
        Vector2f sizeOffset = new Vector2f((float) -Math.floor(lowestSize.x), (float) -Math.floor(lowestSize.y));

        // The heights of a rounded point of this model.
        // This is only used if the model type is Terrain.
        float[][] heights = new float[(int) size.x + 1][(int) size.y + 1];

        for (Triangle triangle : data.triangles) {
            addHeight(heights, sizeOffset, triangle, vertices);
        }

        RawModel model = GLLoader.getLoader().loadToVao(data.positions, data.colors, data.normals, data.indices, splitLine[1]);

        int entityId = EntityManager.getManager().create();
        return new Terrain(entityId, model, terrainPosition, terrainRotation, 1, heights, size, sizeOffset);
    }

    private WaterTile createWaterTile(String line) {
        String[] splitLine = line.split(" ");

        String[] position = splitLine[1].split("/");
        Vector2f waterPosition = new Vector2f(Float.parseFloat(position[0]), Float.parseFloat(position[1]));

        int radius = Integer.parseInt(splitLine[2]);

        System.out.println("water position: " + waterPosition);
        System.out.println("water radius: " + radius);

        return new WaterTile(waterPosition, radius);
    }

    /**
     * Load a path to a model.
     *
     * @param path the path to load.
     * @return the model.
     * @throws IOException thrown if something went wrong.
     */
    private ObjectData loadObjectData(Path path) throws IOException {
        if (!checkFilePath(path)) {
            throw new IOException("Could not find path while loading model: " + path);
        }

        System.out.println("Try loading object data of: " + path);
        long startTime = System.currentTimeMillis();

        String originDirectory = String.join("", path.toAbsolutePath().toString().split(path.getFileName().toString()));

        // All the lines of the .obj file.
        List<String> lines = Files.readAllLines(path);

        // The HashMap that contains all the materials with the string as name.
        HashMap<String, Material> materialHashMap = new HashMap<>();
        // Faces are separated by a material so for every material there is a
        HashMap<Material, ArrayList<Integer>> positionIndices = new HashMap<>();

        // A list of every vertex.
        List<Vector3f> vertices = new ArrayList<>();

        // A list of all vertex normals.
        List<Vector3f> normalVectors = new ArrayList<>();

        Vector2f highestSize = new Vector2f(0, 0);
        Vector2f lowestSize = new Vector2f(0, 0);

        for (String line : lines) {
            String[] currentLine = line.split(" ");
            if (line.startsWith("mtllib ")) {
                readMaterialFile(Paths.get(originDirectory + currentLine[1]), materialHashMap);
            } else if (line.startsWith("v ")) {
                Vector3f vertex = parseLine(currentLine[1], currentLine[2], currentLine[3]);
                if (vertex.x > highestSize.x) {
                    highestSize.x = vertex.x;
                }
                if (vertex.z > highestSize.y) {
                    highestSize.y = vertex.z;
                }
                if (vertex.x < lowestSize.x) {
                    lowestSize.x = vertex.x;
                }
                if (vertex.z < lowestSize.y) {
                    lowestSize.y = vertex.z;
                }
                vertices.add(vertex);
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
        for (String line : lines) {
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
        }

        // Array of all positions that have a different color.
        // So a position can occur twice but that is because one has a different color.
        float[] positions = new float[distinctVertexCounter.getValue() * 3];

        // The color attached to a position.
        float[] colors = new float[distinctVertexCounter.getValue() * 3];

        // The normal that is attached to a position.
        float[] normals = new float[distinctVertexCounter.getValue() * 3];

        // Array of all indices with the length of all vertices of each face.
        int[] indices = new int[indicesCount.getValue()];

        // The x size.
        int xSize = (int) (Math.ceil(highestSize.x) - Math.floor(lowestSize.x));
        // This is the zSize and not y because in 3D it is the z axis.
        int zSize = (int) (Math.ceil(highestSize.y) - Math.floor(lowestSize.y));
        Vector2f size = new Vector2f(xSize, zSize);

        Vector2f sizeOffset = new Vector2f((float) -Math.floor(lowestSize.x), (float) -Math.floor(lowestSize.y));

        // The heights of a rounded point of this model.
        // This is only used if the model type is Terrain.
        float[][] heights = new float[(int) size.x + 1][(int) size.y + 1];


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

            // Calculate the heights of whole points inside this triangle.
            heights = addHeight(heights, sizeOffset, triangle, vertices);


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

            trianglePointer++;
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Object data loaded in: " + duration + " millis");

        return new ObjectData(triangles, vertices, normalVectors, materialHashMap, positions, colors, normals, indices, heights, size, sizeOffset);
    }

    private List<Vector3f> transformVertices(Matrix4f transformationMatrix, List<Vector3f> vertices) {
        List<Vector3f> transformedVertices = new ArrayList<>();
        for (Vector3f vertex : vertices) {
            Vector4f transformedVertex = Maths.matrixVectorMultiplication(transformationMatrix, new Vector4f(vertex.x, vertex.y, vertex.z, 1));
            transformedVertices.add(new Vector3f(transformedVertex.x, transformedVertex.y, transformedVertex.z));
        }
        return transformedVertices;
    }


    /**
     * Add the heights within a triangle to the heights array.
     *
     * @param heights the heights of the model.
     * @param sizeOffset the offset relative to size of the model.
     * @param triangle the current triangle that is being tested
     * @param vertices all the vertices of the whole model.
     * @return The array with the heights of all whole points.
     */
    private float[][] addHeight(float[][] heights, Vector2f sizeOffset, Triangle triangle, List<Vector3f> vertices) {
        Vector3f positionA = vertices.get(triangle.vertex1 - 1);

        // The position relative to the size of the model. (only positive numbers)
        positionA = new Vector3f(positionA.x + sizeOffset.x, positionA.y, positionA.z + sizeOffset.y);
        // The base position. (Only rounded numbers)
        Vector2f positionABase = new Vector2f((float) Math.floor(positionA.x), (float) Math.floor(positionA.z));

        Vector3f positionB = vertices.get(triangle.vertex2 - 1);
        positionB = new Vector3f(positionB.x + sizeOffset.x, positionB.y, positionB.z + sizeOffset.y);
        Vector2f positionBBase = new Vector2f((float) Math.floor(positionB.x), (float) Math.floor(positionB.z));

        Vector3f positionC = vertices.get(triangle.vertex3 - 1);
        positionC = new Vector3f(positionC.x + sizeOffset.x, positionC.y, positionC.z + sizeOffset.y);
        Vector2f positionCBase = new Vector2f((float) Math.floor(positionC.x), (float) Math.floor(positionC.z));

        Vector2f lowestBase = new Vector2f(positionABase.x, positionABase.y);
        Vector2f highestBase = new Vector2f(positionABase.x, positionABase.y);

        // Get the highest and lowest base.
        if (positionBBase.x < lowestBase.x) lowestBase.x = positionBBase.x;
        if (positionBBase.y < lowestBase.y) lowestBase.y = positionBBase.y;

        if (positionCBase.x < lowestBase.x) lowestBase.x = positionCBase.x;
        if (positionCBase.y < lowestBase.y) lowestBase.y = positionCBase.y;

        if (positionBBase.x > highestBase.x) highestBase.x = positionBBase.x;
        if (positionBBase.y > highestBase.y) highestBase.y = positionBBase.y;

        if (positionCBase.x > highestBase.x) highestBase.x = positionCBase.x;
        if (positionCBase.y > highestBase.y) highestBase.y = positionCBase.y;

        // Loop through every point between the lowest and highest base and check if that point is within
        // the current triangle. If so, calculate the height of the of the point and store it within the heights array.
        for (int x = (int) lowestBase.x; x <= highestBase.x; x++) {
            for (int z = (int) lowestBase.y; z <= highestBase.y; z++) {
                boolean isWithin = isWithinTriangle(
                        new Vector2f(x, z),
                        new Vector2f(positionA.x, positionA.z),
                        new Vector2f(positionB.x, positionB.z),
                        new Vector2f(positionC.x, positionC.z)
                );

                if (!isWithin) continue;

                heights[x][z] = Maths.baryCentric(positionA, positionB, positionC, new Vector2f(x, z));
            }
        }
        return heights;
    }

    /**
     * Checks if a point is within a triangle.
     *
     * @param position the point to check for.
     * @param pointA point 1 of the triangle.
     * @param pointB point 2 of the triangle.
     * @param pointC point 3 of the triangle.
     * @return if the @position is within the triangle return true. Otherwise it will return false.
     */
    private boolean isWithinTriangle(Vector2f position, Vector2f pointA, Vector2f pointB, Vector2f pointC) {
        if (position.x == pointA.x && position.y == pointA.y) return true;
        if (position.x == pointB.x && position.y == pointB.y) return true;
        if (position.x == pointC.x && position.y == pointC.y) return true;

        boolean b1, b2, b3;

        b1 = sign(position, pointA, pointB) < 0.0f;
        b2 = sign(position, pointB, pointC) < 0.0f;
        b3 = sign(position, pointC, pointA) < 0.0f;

        return ((b1 == b2) && (b2 == b3));
    }

    private float sign(Vector2f s, Vector2f pointA, Vector2f pointB) {
        return (s.x - pointB.x) * (pointA.y - pointB.y) - (pointA.x - pointB.x) * (s.y - pointB.y);
    }

    /**
     * Checks if a path is a file and if it has the .obj extension.
     *
     * @param filePath the path to check.
     * @return false if directory or other extension.
     */
    private boolean checkFilePath(Path filePath) {
        if (Files.isDirectory(filePath)) {
            return false;
        }
        String fileName = filePath.getFileName().toString();
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).equals("obj");
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

    /**
     * Adds the expected path to a name.
     *
     * @param name the name of the model.
     * @return An expected path to the model.
     * @throws IOException thrown if the file doesn't exists.
     */
    private Path parsePath(String name) throws IOException {
        Path filePath = Paths.get(System.getProperty("user.dir") + "\\res\\save\\" + name + "\\" + name + ".obj");
        if (!Files.exists(filePath)) {
            throw new IOException("Expected object path doesn't exists: " + filePath.toString());
        }
        return filePath;
    }

    /**
     * Parse 3 string to floats and return a vector.
     *
     * @param first the first point
     * @param second the second point
     * @param third the third point
     * @return a vector containing all the points.
     */
    private Vector3f parseLine(String first, String second, String third) {
        return new Vector3f(Float.parseFloat(first), Float.parseFloat(second), Float.parseFloat(third));
    }

    /**
     * Reads a .mtl file and add every material to the {@param materialHashMap}
     *
     * @param file the path to the .mtl file.
     * @param materialHashMap the HashMap containing all the materials.
     */
    private void readMaterialFile(Path file, HashMap<String, Material> materialHashMap) {
        StringBuilder fileContent = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
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

        int getValue() {
            return value;
        }

        void increment() {
            value++;
        }
    }

    private class ObjectData {
        List<Triangle> triangles;
        List<Vector3f> vertices;
        List<Vector3f> normalVectors;
        HashMap<String, Material> materialHashMap;
        float[] positions;
        float[] colors;
        float[] normals;
        int[] indices;
        float[][] heights;
        Vector2f size;
        Vector2f sizeOffset;

        ObjectData(List<Triangle> triangles,
                   List<Vector3f> vertices,
                   List<Vector3f> normalVectors,
                   HashMap<String, Material> materialHashMap,
                   float[] positions,
                   float[] colors,
                   float[] normals,
                   int[] indices,
                   float[][] heights,
                   Vector2f size,
                   Vector2f sizeOffset) {
            this.triangles = triangles;
            this.vertices = vertices;
            this.normalVectors = normalVectors;
            this.materialHashMap = materialHashMap;
            this.positions = positions;
            this.colors = colors;
            this.normals = normals;
            this.indices = indices;
            this.heights = heights;
            this.size = size;
            this.sizeOffset = sizeOffset;
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
