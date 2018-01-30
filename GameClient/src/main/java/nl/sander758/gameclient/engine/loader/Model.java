package nl.sander758.gameclient.engine.loader;

import nl.sander758.gameclient.engine.utils.Maths;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// TODO update this class
public class Model {

    private Mesh mesh;
    private String modelPath;

    // The HashMap that contains all the materials with the string as name.
    private HashMap<String, Material> materialHashMap = new HashMap<>();

    // Faces are separated by a material so for every material there is a
    private HashMap<Material, ArrayList<Integer>> positionIndices = new HashMap<>();

    private ClassLoader loader = Model.class.getClassLoader();

    private float[][] heights;
    private Vector2f sizeOffset;

    // Other stuff maybe animation info

    public Model(String modelPath) {
        this.modelPath = modelPath;
        try {
            loadModel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Mesh getMesh() {
        return mesh;
    }

    public float[][] getHeights() {
        return heights;
    }

    public Vector2f getSizeOffset() {
        return sizeOffset;
    }

    private void loadModel() throws IOException {
        InputStream inputStream = loader.getResourceAsStream(modelPath);

        if (inputStream == null) {
            throw new IOException("File not found");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = new ArrayList<>();
        String readerLine;
        while ((readerLine = reader.readLine()) != null) {
            lines.add(readerLine);
        }
        reader.close();

        String[] splitPath = modelPath.split("/");
        splitPath[splitPath.length - 1] = "";
        String originDirectory = String.join("/", splitPath);


        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normalVectors = new ArrayList<>();


        Vector2f highestSize = new Vector2f(0, 0);
        Vector2f lowestSize = new Vector2f(0, 0);

        for (String line : lines) {
            String[] currentLine = line.split(" ");
            if (line.startsWith("mtllib ")) {
                readMaterialFile(originDirectory + currentLine[1]);
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
        this.sizeOffset = sizeOffset;

        // The heights of a rounded point of this model.
        // This is only used if the model type is Terrain.
        heights = new float[(int) size.x + 1][(int) size.y + 1];


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

        mesh = MeshLoader.load3DMesh(positions, normals, colors, indices);
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
     * Reads a .mtl file and add every material to the materialHashMap
     *
     * @param file the path to the .mtl file.
     */
    private void readMaterialFile(String file) {
        StringBuilder fileContent = new StringBuilder();
        System.out.println("try loading material file: " + file);
        try {
            InputStream inputStream = loader.getResourceAsStream(file);
            if (inputStream == null) {
                throw new IOException("File does not exists");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String readerLine;
            while ((readerLine = reader.readLine()) != null) {
                fileContent.append(readerLine).append("\n");
            }
            reader.close();

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
                    color.x = Float.parseFloat(diffuseColor[1]);
                    color.y = Float.parseFloat(diffuseColor[2]);
                    color.z = Float.parseFloat(diffuseColor[3]);
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
