package dk.kea.androidgame.martin.myfirstgameengine.breakoutGame

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import dk.kea.androidgame.martin.myfirstgameengine.core.GameEngine
import dk.kea.androidgame.martin.myfirstgameengine.core.Screen
import dk.kea.androidgame.martin.myfirstgameengine.touch.TouchEvent

class GameScreen(gameEngine: GameEngine) : Screen(gameEngine) {
    enum class State {
        PAUSED,
        RUNNING,
        GAME_OVER
    }

    var background: Bitmap = gameEngine.loadBitmap("background.png")
    var resume: Bitmap = gameEngine.loadBitmap("resume.png")
    var gameOver: Bitmap = gameEngine.loadBitmap("gameover.png")
    var state: State = State.RUNNING
    var world: World = World()
    var worldRenderer: WorldRenderer = WorldRenderer(gameEngine, world)
    var font: Typeface = gameEngine.loadFont("font.ttf")
    var showText = "Nothing good to show"


    /*
    * todo: fix music to avoid crashes
    * */
    override fun update(deltaTime: Float) {
        if (world.lostLife) {
            state = State.PAUSED
            world.lostLife = false
        }

        if (world.gameOver) {
            state = State.GAME_OVER
        }

        if (state === State.PAUSED && gameEngine.isTouchDown(0)) { // if paused
            state = State.RUNNING
            resume()
        }

        if (state === State.GAME_OVER && gameEngine.isTouchDown(0)) {
            pause()
            gameEngine.drawBitmap(gameOver, (160 - gameOver.width / 2).toFloat(), (240 - gameOver.height / 2).toFloat())

            val events = gameEngine.getTouchEvents()
            for (event in events) {
                if (event.type === TouchEvent.TouchEventType.UP) {
                    gameEngine.setScreen(MainMenuScreen(gameEngine))
                    return
                }
            }
        }

        if (state === State.RUNNING && gameEngine.getTouchY(0) < 35 && gameEngine.getTouchX(0) > 320 - 35) {
            state = State.PAUSED
            pause()
            return
        }

        gameEngine.drawBitmap(background, 0f, 0f)

        if (state === State.RUNNING) {
            resume()
            world.update(deltaTime, gameEngine.accelerometer[0], gameEngine.isTouchDown(0), gameEngine.getTouchX(0))
        }
        worldRenderer.render()

        showText = "Lives: ${world.lives}  Points: ${world.points}"
        gameEngine.drawText(font = font, text = showText, x = 22f, y = 22f, color = Color.GREEN, size = 12f)

        if (state === State.PAUSED) {
            pause()
            gameEngine.drawBitmap(resume, (160 - resume.width / 2).toFloat(), (240 - resume.height / 2).toFloat())
        }

        if (state === State.GAME_OVER) {
            pause()
            gameEngine.drawBitmap(gameOver, (160 - gameOver.width / 2).toFloat(), (240 - gameOver.height / 2).toFloat())
        }
    }

    override fun pause() {
        gameEngine.music.pause()
    }

    override fun resume() {
        gameEngine.music.play()
    }

    override fun dispose() {
        gameEngine.music.dispose()
    }

}
