package com.gadarts.war.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameC.Menu.MainMenu;
import com.gadarts.war.GameSettings;
import com.gadarts.war.Profiler;
import com.gadarts.war.menu.GameMenu;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.systems.CameraSystem;
import com.gadarts.war.systems.render.RenderSystem;

import java.io.File;
import java.util.ArrayList;

public class MainMenuScreen extends BaseGameScreen {

    private static Vector3 auxVector31 = new Vector3();
    private static Vector3 auxVector32 = new Vector3();
    private final Stage stage = new Stage();
    private ArrayList<CactusDec> cacti = new ArrayList<>();
    private ModelBatch modelBatch = new ModelBatch();
    private Environment environment = new Environment();
    private PerspectiveCamera cam;
    private CameraInputController debugInputProcessor;
    private Matrix4 auxMatrix = new Matrix4();
    private GameMenu menu;
    private Profiler profiler;
    private Texture background;
    private ShaderProgram backgroundShaderProgram;
    private MainMenuBackground backgroundObject;

    @Override
    public void show() {
        super.show();
        cam = CameraSystem.createCamera();
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        cam.translate(width / 100f, -height / 100f, MainMenu.Camera.Z);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().setDirection(-1, -0.5f, -0.5f).setColor(0.6f, 0.6f, 0.6f, 1f));
        createCacti();
        if (GameSettings.SPECTATOR) {
            debugInputProcessor = CameraSystem.createAndSetDebugInputProcessor(cam);
        }
        menu = new GameMenu(this);
        menu.initialize(stage);
        menu.setVisible(true);
        stage.setDebugAll(GameSettings.DRAW_TABLES_BORDERS);
        stage.setViewport(new ScreenViewport(stage.getCamera()));
        profiler = new Profiler(stage);
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 0, 0, 1);
        pixmap.fillRectangle(0, 0, width, height);
        background = new Texture(pixmap);
        pixmap.dispose();
        String vertexShader = Gdx.files.internal("shaders" + File.separator + "vertex_main_menu_background.glsl").readString();
        ShaderProgram.pedantic = false;
        String fragmentShader = Gdx.files.internal("shaders" + File.separator + "fragment_main_menu_background.glsl").readString();
        backgroundShaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        backgroundObject = new MainMenuBackground(background, backgroundShaderProgram);
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        modelBatch.dispose();
        background.dispose();
        backgroundShaderProgram.dispose();
    }

    private void createCacti() {
        for (int i = 0; i < MainMenu.NUMBER_OF_CACTI; i++) {
            String fileName = getRandomCactusModel();
            ModelInstance modelInstance = createCactusModelInstance(fileName);
            int initialSpeed = MathUtils.random(MainMenu.MIN_SPEED, MainMenu.MAX_SPEED);
            CactusDec cactus = new CactusDec(modelInstance, initialSpeed);
            resetCactus(cactus);
            cacti.add(cactus);
        }
    }

    private String getRandomCactusModel() {
        int dice = MathUtils.random(2);
        String result;
        if (dice == 0) {
            result = GameC.Files.MODELS_FOLDER_NAME + "/" + "menu_dec_barrel.g3dj";
        } else if (dice == 1) {
            result = GameC.Files.MODELS_FOLDER_NAME + "/" + "menu_dec_saguaro.g3dj";
        } else {
            result = GameC.Files.MODELS_FOLDER_NAME + "/" + "menu_dec_prickly.g3dj";
        }
        return result;
    }

    private ModelInstance createCactusModelInstance(String fileName) {
        ModelInstance modelInstance = new ModelInstance(GameAssetManager.getInstance().get(fileName, Model.class));
        Matrix4 transform = modelInstance.transform;
        transform.rotate(Vector3.X, MathUtils.random() * 360f);
        transform.rotate(Vector3.Y, MathUtils.random() * 360f);
        transform.rotate(Vector3.Z, MathUtils.random() * 360f);
        return modelInstance;
    }

    private void resetCactus(CactusDec cactus) {
        float x = MathUtils.randomBoolean() ? -1f : Gdx.graphics.getWidth() / 50f;
        float y = MathUtils.random() * Gdx.graphics.getHeight() / 50f;
        cactus.getRotationVector().set(MathUtils.random(), MathUtils.random(), MathUtils.random()).setLength2(MathUtils.random(MainMenu.MIN_SPEED, MainMenu.MAX_SPEED));
        ModelInstance modelInstance = cactus.getModelInstance();
        modelInstance.transform.setTranslation(x, -y, 0);
        cactus.setBeenInside(false);
        Vector3 camPosition = cam.position;
        Vector3 translation = modelInstance.transform.getTranslation(auxVector32);
        Vector3 directionToCenter = auxVector31.set(camPosition.x, camPosition.y, 0).sub(translation.x, translation.y, 0);
        Vector2 movingVector = cactus.getMovingVector();
        directionToCenter.nor().setLength2(movingVector.len2());
        movingVector.set(directionToCenter.x, directionToCenter.y);
        BoundingBox boundingBox = cactus.getBoundingBox();
        modelInstance.calculateBoundingBox(boundingBox.clr());
        boundingBox.mul(auxMatrix.idt().scl(0.9f));
        boundingBox.mul(auxMatrix.setTranslation(translation));
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);
        if (debugInputProcessor != null) debugInputProcessor.update();
        cam.update();
        RenderSystem.resetDisplay(Color.BLACK);
        backgroundObject.draw(stage.getBatch(), 1f);
        renderCacti(deltaTime);
        stage.act(deltaTime);
        stage.draw();
        profiler.update();
        profiler.reset();
    }

    @Override
    public void onEscPressed() {
        Gdx.app.log("!", "!");
    }

    private void renderCacti(float deltaTime) {
        modelBatch.begin(cam);
        for (CactusDec cactus : cacti) {
            Vector2 movingVector = cactus.getMovingVector();
            ModelInstance modelInstance = cactus.getModelInstance();
            Matrix4 transform = modelInstance.transform;
            float deltaX = movingVector.x * deltaTime;
            float deltaY = movingVector.y * deltaTime;
            transform.trn(deltaX, deltaY, 0);
            Vector3 rotationVector = cactus.getRotationVector();
            transform.rotate(Vector3.X, rotationVector.x);
            transform.rotate(Vector3.Y, rotationVector.y);
            transform.rotate(Vector3.Z, rotationVector.z);
            BoundingBox boundingBox = cactus.getBoundingBox();
            boundingBox.set(boundingBox.min.add(deltaX, deltaY, 0), boundingBox.max.add(deltaX, deltaY, 0));
            if (cactus.isBeenInside()) {
                for (CactusDec otherCactus : cacti) {
                    if (otherCactus != cactus && cactus.getLastCactusCollider() != otherCactus) {
                        if (otherCactus.isBeenInside() && otherCactus.getBoundingBox().intersects(boundingBox)) {
                            Vector3 cactusMovingVector = transform.getTranslation(auxVector32).sub(otherCactus.getModelInstance().transform.getTranslation(auxVector31)).setLength2(cactus.getMovingVector().len2());
                            cactus.getMovingVector().set(cactusMovingVector.x, cactusMovingVector.y);
                            cactus.setLastCactusCollider(otherCactus);
                        }
                    }
                }
            }
            boolean inside = cam.frustum.pointInFrustum(transform.getTranslation(auxVector31));
            if (cactus.isBeenInside()) {
                if (!inside && !cam.frustum.boundsInFrustum(boundingBox)) {
                    resetCactus(cactus);
                }
            } else if (inside) {
                cactus.setBeenInside(true);
            }
            modelBatch.render(modelInstance, environment);
        }
        modelBatch.end();
    }
}
