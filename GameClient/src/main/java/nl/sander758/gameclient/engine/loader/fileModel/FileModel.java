package nl.sander758.gameclient.engine.loader.fileModel;

import nl.sander758.gameclient.engine.loader.Mesh;
import nl.sander758.gameclient.engine.loader.Model;
import nl.sander758.gameclient.engine.loader.ModelLoadingException;
import nl.sander758.gameclient.engine.loader.VBO;
import nl.sander758.gameclient.engine.loader.fileModel.*;
import nl.sander758.gameclient.engine.utils.Pair;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileModel extends Model {

    private String filePath;

    public FileModel(String filePath) {
        this.filePath = filePath;
    }

    @Override
    protected void load() throws ModelLoadingException {
        List<String> content = readFileToStringList(filePath);
        HashMap<String, Material> materials = findMaterials(content, filePath);
        List<Vertex> vertices = findVertices(content);
        List<Normal> normals = findNormals(content);

        Pair<HashMap<Material, List<VertexNormalCombination>>, HashMap<Material, List<Face>>> facesData = findFaces(content, materials, filePath);

        HashMap<Material, List<VertexNormalCombination>> vertexNormalCombinations = facesData.getKey();
        HashMap<Material, List<Face>> materialFaces = facesData.getValue();

        int vertexNormalCombinationSum = 0;
        for (List<VertexNormalCombination> vertexNormalCombinationList : vertexNormalCombinations.values()) {
            vertexNormalCombinationSum += vertexNormalCombinationList.size();
        }

        float[] verticesArray = new float[vertexNormalCombinationSum * 3];
        float[] normalsArray = new float[vertexNormalCombinationSum * 3];
        float[] colors = new float[vertexNormalCombinationSum * 3];

        for (Material material : vertexNormalCombinations.keySet()) {
            for (VertexNormalCombination combination : vertexNormalCombinations.get(material)) {
                Vertex vertex = vertices.get(combination.vertexId - 1);
                Normal normal = normals.get(combination.normalId - 1);

                verticesArray[combination.id * 3] = vertex.vertex.x;
                verticesArray[combination.id * 3 + 1] = vertex.vertex.y;
                verticesArray[combination.id * 3 + 2] = vertex.vertex.z;

                normalsArray[combination.id * 3] = normal.normal.x;
                normalsArray[combination.id * 3 + 1] = normal.normal.y;
                normalsArray[combination.id * 3 + 2] = normal.normal.z;

                colors[combination.id * 3] = material.diffuseColor.x;
                colors[combination.id * 3 + 1] = material.diffuseColor.y;
                colors[combination.id * 3 + 2] = material.diffuseColor.z;
            }
        }

        int indexCount = 0;
        for (Material material : materialFaces.keySet()) {
            for (Face face : materialFaces.get(material)) {
                if (face.vertexNormalCombinationIndices.size() < 3) {
                    continue;
                }
                indexCount += face.vertexNormalCombinationIndices.size() - 2;
            }
        }

        int[] indices = new int[indexCount * 3];

        int indicesPointer = 0;
        for (Material material : materialFaces.keySet()) {
            for (Face face : materialFaces.get(material)) {
                List<Integer> faceIndices = face.toTriangles();
                for (Integer index : faceIndices) {
                    indices[indicesPointer] = index;
                    indicesPointer++;
                }
            }
        }

        Mesh mesh = new Mesh();
        mesh.bindVAO();
        mesh.attachVBO(new VBO(0, 3, verticesArray));
        mesh.attachVBO(new VBO(1, 3, normalsArray));
        mesh.attachVBO(new VBO(2, 3, colors));
        mesh.attachVBO(new VBO(indices));
        mesh.unbindVAO();
        mesh.setVertexCount(indices.length);
        meshes.add(mesh);
    }

    private Pair<HashMap<Material, List<VertexNormalCombination>>, HashMap<Material, List<Face>>> findFaces(List<String> content, HashMap<String, Material> materials, String path) throws ModelLoadingException {
        if (materials.isEmpty()) {
            throw new ModelLoadingException("No materials found while trying to load faces of: " + path);
        }

        int combinationCount = 0;
        HashMap<Material, List<VertexNormalCombination>> combinations = new HashMap<>();
        HashMap<Material, List<Face>> materialFaces = new HashMap<>();

        for (Material material : materials.values()) {
            combinations.put(material, new ArrayList<>());
            materialFaces.put(material, new ArrayList<>());
        }

        Material currentMaterial = materials.values().iterator().next();
        for (String line : content) {
            if (!line.startsWith("f ") && !line.startsWith("usemtl ")) {
                continue;
            }
            String[] splitLine = line.split(" ");
            if (line.startsWith("usemtl ") && splitLine.length >= 2) {
                currentMaterial = materials.get(splitLine[1]);
                continue;
            }

            Face face = new Face();

            for (int i = 1; i < splitLine.length; i++) {
                String[] splitVertex = splitLine[i].split("/");

                VertexNormalCombination currentCombination = new VertexNormalCombination(combinationCount, Integer.parseInt(splitVertex[0]), Integer.parseInt(splitVertex[2]));

                boolean unique = true;
                for (VertexNormalCombination combination : combinations.get(currentMaterial)) {
                    if (combination.equals(currentCombination)) {
                        unique = false;
                        currentCombination = combination;
                        break;
                    }
                }
                if (unique) {
                    combinations.get(currentMaterial).add(currentCombination);
                    combinationCount++;
                }

                face.addVertex(currentCombination.id);
            }
            materialFaces.get(currentMaterial).add(face);
        }

        return new Pair<>(combinations, materialFaces);
    }

    private List<Normal> findNormals(List<String> content) {
        List<Normal> normals = new ArrayList<>();

        for (String line : content) {
            if (!line.startsWith("vn ")) {
                continue;
            }
            String[] splitLine = line.split(" ");
            if (splitLine.length < 4) {
                continue;
            }
            normals.add(new Normal(new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]))));
        }

        return normals;
    }

    private List<Vertex> findVertices(List<String> content) {
        List<Vertex> vertices = new ArrayList<>();

        for (String line : content) {
            if (!line.startsWith("v ")) {
                continue;
            }
            String[] splitLine = line.split(" ");
            if (splitLine.length < 4) {
                continue;
            }
            vertices.add(new Vertex(new Vector3f(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]), Float.parseFloat(splitLine[3]))));
        }

        return vertices;
    }

    private HashMap<String, Material> findMaterials(List<String> content, String path) throws ModelLoadingException {
        HashMap<String, Material> materials = new HashMap<>();

        String[] splitPath = path.split("/");
        splitPath[splitPath.length - 1] = "";
        String originDirectory = String.join("/", splitPath);

        for (String line : content) {
            if (!line.startsWith("mtllib ")) {
                continue;
            }
            String[] splitLine = line.split(" ");
            if (splitLine.length <= 1) {
                continue;
            }
            for (Material material : readMaterialsFile(originDirectory + splitLine[1])) {
                materials.put(material.name, material);
            }
        }
        return materials;
    }

    private List<Material> readMaterialsFile(String materialFile) throws ModelLoadingException {
        String content = String.join("\n", readFileToStringList(materialFile));
        String[] materials = content.split("newmtl ");

        List<Material> materialList = new ArrayList<>();

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
            // Add the new material to the list
            materialList.add(new Material(materialName, color));
        }
        return materialList;
    }

    private List<String> readFileToStringList(String path) throws ModelLoadingException {
        ClassLoader loader = Model.class.getClassLoader();
        List<String> lines = new ArrayList<>();

        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = loader.getResourceAsStream(path);
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String readerLine;
            while ((readerLine = reader.readLine()) != null) {
                lines.add(readerLine);
            }
        } catch (IOException e) {
            throw new ModelLoadingException("Could not load file: " + path + " with message: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }
}
