package edu.anu.comp6442.retrogame2018s1.view;

/*
 * Copyright (C) 2018,
 *
 * Haotian Shi <u6158063@anu.edu.au>
 * Jiewei Qian <u7472740@anu.edu.au>
 * Yang Zheng <u6287751@anu.edu.au>
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import edu.anu.comp6442.retrogame2018s1.FpsCounter;
import edu.anu.comp6442.retrogame2018s1.IntervalScheduler;
import edu.anu.comp6442.retrogame2018s1.R;
import edu.anu.comp6442.retrogame2018s1.model.AdvancedEnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.Bonus;
import edu.anu.comp6442.retrogame2018s1.model.BossBullet;
import edu.anu.comp6442.retrogame2018s1.model.BossEnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.Bullet;
import edu.anu.comp6442.retrogame2018s1.model.BulletBonus;
import edu.anu.comp6442.retrogame2018s1.model.EnemyBullet;
import edu.anu.comp6442.retrogame2018s1.model.EnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.HpBonus;
import edu.anu.comp6442.retrogame2018s1.model.MediumEnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.PlayerBullet;
import edu.anu.comp6442.retrogame2018s1.model.PlayerPlane;
import edu.anu.comp6442.retrogame2018s1.model.PrimaryEnemyPlane;
import edu.anu.comp6442.retrogame2018s1.model.Scoreboard;
import edu.anu.comp6442.retrogame2018s1.model.Sprite;

import static edu.anu.comp6442.retrogame2018s1.Utility.clampRange;

public class GameView extends View {

    // game states
    public static final int GAME_PLAYING = 0;
    public static final int GAME_OVER = 1;
    public static int gameStatus = -1;

    // update / repaint interval, in milliseconds
    // actual repaint may be slower if phone cpu is poor
    // 16 -> 60fps
    // 33 -> 30fps
    public static int UPDATE_TICK_INTERVAL = 16;
    public static int REPAINT_TICK_INTERVAL = 16;

    // touch state and position, determined whether plane is moved in each tick
    private static boolean keepMoving = false;
    private static float targetX = 0;
    private static float targetY = 0;

    private static int tick = 0;   // tick counter

    private static int score = 0;  // player score in this game

    private static float density = 1;  // screen pixel density

    // control plane is displayed how much above the touch location
    // so player's fingertip will not block view to the plane
    private static float fingertipOffsetFactor = 1.2f;

    // all image resources
    public static List<Bitmap> images;
    private static List<Bitmap> explosionBmps;

    // enemy planes and bullets
    private static List<Sprite> sprites;
    private static List<Sprite> readySprites;

    // timer for repaint and update
    // different timers are used, so update happens independently of repaint performance
    IntervalScheduler repaintInterval;
    IntervalScheduler updateInterval;

    private Paint paint;
    private Paint textPaint;

    // important sprites
    private PlayerPlane player;
    private BossEnemyPlane boss;
    public static Bonus bonus;
    private List<Bullet> playerBullets;

    // width and height of game(canvas) area
    private int width;
    private int height;

    private float fontSize = 12;  //default font size
    private float fontSize2 = 20;  //font size for game over dialog
    private float borderSize = 2;  // border size for game over dialog
    private Rect continueRect = new Rect();  // restart button

    private Scoreboard scoreBoard;
    private FpsCounter fps = new FpsCounter();

    public GameView(Context context) {
        super(context);
        loadResources();
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadResources();
        init();
    }

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadResources();
        init();
    }

    /**
     * Get the player bullets exist in the screen.
     */
    public static List<Bullet> getActivePlayerBullets() {
        List<Bullet> bullets = new ArrayList<>();
        for (Sprite s : sprites) {
            if (s instanceof PlayerBullet && !s.isDestroyed())
                bullets.add((Bullet) s);
        }
        return bullets;
    }

    /**
     * Get the enemy bullets exist in the screen.
     */
    public static List<Bullet> getActiveEnemyBullets() {
        List<Bullet> bullets = new ArrayList<>();
        for (Sprite s : sprites) {
            if (s instanceof EnemyBullet && !s.isDestroyed())
                bullets.add((Bullet) s);
        }
        return bullets;
    }

    /**
     * Get the enemy planes (except the boss) which can fire bullets.
     */
    public List<EnemyPlane> getArmedEnemyPlanes() {
        List<EnemyPlane> planes = new ArrayList<>();
        for (Sprite s : sprites) {
            if (s instanceof AdvancedEnemyPlane && !s.isDestroyed())
                planes.add((EnemyPlane) s);
        }
        return planes;
    }

    public static float getDensity() {
        return density;
    }

    public static Bonus getBonus() {
        return bonus;
    }

    public static List<Bitmap> getExplosionBitmaps() {
        return explosionBmps;
    }

    public static void addSprite(Sprite sprite) {
        readySprites.add(sprite);
    }

    public static void addScore(int s) {
        score += s;
    }

    /**
     * load all bitmap resources
     * if already loaded, do nothing
     */
    public void loadResources() {
        if (images == null) {
            images = new ArrayList<>();
            int[] bitmapId = {
                    R.drawable.player,
                    R.drawable.player2,
                    R.drawable.enemy1,
                    R.drawable.enemy2,
                    R.drawable.enemy3,
                    R.drawable.enemy4,
                    R.drawable.bullet1,
                    R.drawable.bullet2,
                    R.drawable.bullet3,
                    R.drawable.bonus1,
                    R.drawable.bonus2,
                    R.drawable.bonus3
            };
            for (int id : bitmapId) {
                images.add(loadBitmap(id));
            }
        }

        if (explosionBmps == null) {
            int[] explosionBitmapIds = {
                    R.drawable.explosion_1,
                    R.drawable.explosion_3,
                    R.drawable.explosion_5,
                    R.drawable.explosion_7,
                    R.drawable.explosion_9,
                    R.drawable.explosion_11,
                    R.drawable.explosion_13,
                    R.drawable.explosion_15,
            };
            explosionBmps = new ArrayList<>();
            for (int id : explosionBitmapIds) {
                explosionBmps.add(loadBitmap(id));
            }
        }
    }

    /**
     * initialize a game
     */
    public void init() {
        setBackgroundResource(R.drawable.bg);
        scoreBoard = new Scoreboard(this.getContext());
        score = 0;
        tick = 0;
        density = getResources().getDisplayMetrics().density;
        keepMoving = false;
        gameStatus = GAME_PLAYING;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        textPaint.setColor(0xff000000);
        fontSize = textPaint.getTextSize();
        fontSize *= density;
        fontSize2 *= density;
        textPaint.setTextSize(fontSize);
        borderSize *= density;

        sprites = new ArrayList<>();
        readySprites = new ArrayList<>();

        player = new PlayerPlane(images.get(0));
        boss = null;
        playerBullets = new ArrayList<>();

        // aim for 30 fps
        repaintInterval = new IntervalScheduler(REPAINT_TICK_INTERVAL, new Runnable() {
            public void run() {
                postInvalidate();
            }
        });
        repaintInterval.start();

        // don't start updating internal unless view is being printed
        updateInterval = new IntervalScheduler(UPDATE_TICK_INTERVAL, new Runnable() {
            public void run() {
                updateGameStates();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();

        if (gameStatus == GAME_PLAYING) {
            updateInterval.start();
            drawGamePlaying(canvas);
        } else if (gameStatus == GAME_OVER) {
            drawGameOver(canvas);
        }

        fps.count();
        drawFps(canvas);
    }

    /**
     * initialize player's state
     */
    public void initializeGameStates(float width, float height) {
        float x = width / 2;
        float y = height - player.getHeight() / 2;
        player.setDestroyed(false);
        player.centerTo(x, y);
        keepMoving = false;
        targetX = x;
        targetY = y;
    }

    /**
     * perform one tick of game
     *
     * determine whether to spawn: enemy, boss, bullets, bonus
     * add time progression (survival) score
     */
    public void updateGameStates() {
        if (tick == 0)
            initializeGameStates(width, height);

        if (player.finishExplosion()) {
            if (gameStatus != GAME_OVER) scoreBoard.add(score);
            gameStatus = GAME_OVER;
            updateInterval.stop();
        } else {
            if (keepMoving) {
                movePlayerPlane();
            }

            playerAttack();
            enemyAttack();

            if (tick % 40 == 0) {
                generateRandomSprites(width);
            }

            if (tick % 1111 == 0) {
                generateRandomBonus();
            }

            // generate the boss enemy when the target score reached
            // score is low for demonstration purpose
            if (score > 300 && boss == null) {
                generateBossEnemy(width);
            }


            int scoreInterval = 1000 / UPDATE_TICK_INTERVAL;
            if (!player.isDestroyed() && tick % scoreInterval == 0) {
                ++score;
            }

            tick += 1;
        }
    }

    /**
     * paint game playing frame
     *
     * removes destroyed sprites, then paint all sprites
     */
    private void drawGamePlaying(Canvas canvas) {
        removeDestroyedSprites();
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()) {
            Sprite s = iterator.next();
            if (s != null) s.draw(canvas, paint);
            // check collision
            checkCollideWithEnemy(s);
        }
        sprites.addAll(readySprites);
        sprites.addAll(playerBullets);
        readySprites.clear();
        playerBullets.clear();

        player.draw(canvas, paint);

        drawScoreAndHp(canvas);
    }

    /**
     * paint game over frame
     *
     * stops repaint and update timer
     * paint score dialog
     */
    private void drawGameOver(Canvas canvas) {
        repaintInterval.stop();
        updateInterval.stop();

        drawScoreDialog(canvas, "Restart");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // if game over, check for restart button click
        // otherwise, record touch state and position
        if (gameStatus == GAME_OVER) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                clickRestartButton(event.getX(), event.getY());
            }
        } else {
            setTouchPosition(event.getX(), event.getY());
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                setTouching(true);
            if (event.getAction() == MotionEvent.ACTION_UP)
                setTouching(false);
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        // pause game if app goes into background
        if (hasWindowFocus) {
            repaintInterval.start();
        } else {
            repaintInterval.stop();
            updateInterval.stop();
        }
    }

    /**
     * Move the player plane towards the touch position.
     *
     * Player will move at most unitDis * player.getSpeed() pixels in each direction in one tick
     * This gives a smooth moving feeling
     */
    private void movePlayerPlane() {
        float fingerOffsetY = fingertipOffsetFactor * player.getHeight();
        float deltaX = targetX - player.getCenterX();
        float deltaY = targetY - player.getCenterY() - fingerOffsetY;    // offset by height so user can see the plane
        float distance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        float unitDis = 3;
        float maxSpeedX = Math.abs(player.getSpeed() * unitDis * deltaX / distance);
        float maxSpeedY = Math.abs(player.getSpeed() * unitDis * deltaY / distance);
        player.move(
                clampRange(deltaX, -maxSpeedX, maxSpeedX),
                clampRange(deltaY, -maxSpeedY, maxSpeedY)
        );
    }

    /**
     * Player fires bullets every 10 ticks.
     */
    private void playerAttack() {
        if (!player.isDestroyed() && tick % 10 == 0) {
            if (player.isSingleBullet()) {
                Bullet bullet = new PlayerBullet(images.get(6));
                bullet.centerTo(player.getCenterX(), player.getY());
                playerBullets.add(bullet);
            } else if (player.isDoubleBullets()) {
                Bullet left = new PlayerBullet(images.get(6));
                Bullet right = new PlayerBullet(images.get(6));
                float unitX = player.getWidth() / 5;
                left.centerTo(player.getX() + unitX, player.getY());
                right.centerTo(player.getX() + unitX * 4, player.getY());
                playerBullets.add(left);
                playerBullets.add(right);
            }
        }
    }

    /**
     * Spawn bullets for armed enemy planes, including the boss plane (if exists).
     */
    private void enemyAttack() {
        if (boss != null && boss.isReadyToAttack() && tick % 40 == 0) {
            float unitX = boss.getWidth() / 6;
            float bulletY = boss.getY() + boss.getHeight();
            Bullet left = new BossBullet(images.get(8));
            Bullet right = new BossBullet(images.get(8));
            left.centerTo(boss.getX() + unitX, bulletY);
            right.centerTo(boss.getX() + boss.getWidth() - unitX, bulletY);
            readySprites.add(left);
            readySprites.add(right);
        }

        if (tick % 55 == 0) {
            // only the armed enemy could fire bullets
            for (EnemyPlane p : getArmedEnemyPlanes()) {
                Bullet bullet = new EnemyBullet(images.get(7));
                bullet.centerTo(p.getCenterX(), 1.5f * p.getY());
                readySprites.add(bullet);
            }
        }
    }

    /**
     * Spawn an enemy plane randomly.
     *
     * Type of plane is based on probability.
     */
    private void generateRandomSprites(int canvasWidth) {
        Sprite sprite;
        Random random = new Random();
        int type = random.nextInt(20);
        if (type < 12) {
            sprite = new PrimaryEnemyPlane(images.get(2));
        } else if (type < 18) {
            sprite = new MediumEnemyPlane(images.get(3));
        } else {
            sprite = new AdvancedEnemyPlane(images.get(4));
        }

        float x = random.nextInt((int) (canvasWidth - sprite.getWidth()));
        float y = -sprite.getHeight();
        sprite.moveTo(x, y);
        readySprites.add(sprite);
    }

    /**
     * Spawn the final boss.
     */
    private void generateBossEnemy(int canvasWidth) {
        boss = new BossEnemyPlane(images.get(5));
        float x = (canvasWidth - boss.getWidth()) / 2;
        float y = -boss.getHeight();
        boss.moveTo(x, y);
        readySprites.add(boss);
    }

    /**
     * Spawn a random bonus.
     * It could be a bonus to add hp or enable the player to fire double bullets.
     */
    private void generateRandomBonus() {
        int type = new Random().nextInt(3);
        if (type == 0) {
            bonus = new HpBonus(images.get(9));
        } else if (type == 1) {
            bonus = new BulletBonus(images.get(10));
        }
        readySprites.add(bonus);
    }

    /**
     * The destroyed sprites should be remove from the list which will be drawn next time.
     */
    private void removeDestroyedSprites() {
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()) {
            Sprite s = iterator.next();
            if (s != null && s.isDestroyed())
                iterator.remove();
        }
    }

    public PlayerPlane getPlayer() {
        return player;
    }
    public List<Bullet> getPlayerBullets() {
        return playerBullets;
    }

    public void setTouchPosition(float x, float y) {
        targetX = x;
        targetY = y;
    }
    public void setTouching(boolean touching) {
        keepMoving = touching;
    }

    /**
     * handle player's collision with enemy plane
     * both planes keeps losing hp until one is destroyed
     *
     * if player's hp is high enough, it kill the boss
     */
    public void checkCollideWithEnemy(Sprite s) {
        if (s instanceof EnemyPlane) {
            if (player.collideWith(s)) {
                int hp = Math.min(player.getHp(), ((EnemyPlane) s).getHp());
                player.removeHp(hp);
                ((EnemyPlane) s).removeHp(hp);
            }
        }
    }

    /**
     * show hp and score at top left corner
     * @param canvas
     */
    private void drawScoreAndHp(Canvas canvas) {
        String str = String.format("HP: %-2d     Score: %d", player.getHp(), score);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(0xFFD7DDDE);
        paint.setTextSize(fontSize);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(str, fontSize / 2, fontSize / 2 + fontSize, paint);
    }

    /**
     * show fps at top right corner
     */
    private void drawFps(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(0xFFD7DDDE);
        paint.setTextSize(fontSize);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format("%d", fps.get()), width - fontSize / 2, fontSize * 1.5f, paint);
    }

    /**
     * show score dialog
     */
    private void drawScoreDialog(Canvas canvas, String operation) {
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        //store original
        float originalFontSize = textPaint.getTextSize();
        Paint.Align originalFontAlign = textPaint.getTextAlign();
        int originalColor = paint.getColor();
        Paint.Style originalStyle = paint.getStyle();

        int w1 = (int) (20.0 / 360.0 * canvasWidth);
        int w2 = canvasWidth - 2 * w1;
        int buttonWidth = (int) (140.0 / 360.0 * canvasWidth);

        int h1 = (int) (150.0 / 558.0 * canvasHeight);
        int h2 = (int) (60.0 / 558.0 * canvasHeight);
        int h3 = (int) (124.0 / 558.0 * canvasHeight);
        int h4 = (int) (76.0 / 558.0 * canvasHeight);
        int buttonHeight = (int) (42.0 / 558.0 * canvasHeight);

        canvas.translate(w1, h1);
        // bg colour
        paint.setStyle(Paint.Style.FILL);
        int color = this.getContext().getResources().getColor(R.color.lavender);
        paint.setColor(color);
        Rect rect1 = new Rect(0, 0, w2, canvasHeight - 2 * h1);
        canvas.drawRect(rect1, paint);
        // draw border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF515151);
        paint.setStrokeWidth(borderSize);

        paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawRect(rect1, paint);
        // draw text Aircraft battle score
        textPaint.setTextSize(fontSize2);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Aircraft Battle Score", w2 / 2, (h2 - fontSize2) / 2 + fontSize2, textPaint);
        // draw lines
        canvas.translate(0, h2);
        canvas.drawLine(0, 0, w2, 0, paint);
        // draw score and highest score
        String yourScore = String.format("%s: %d", "Your Score ", score);
        String topScore = String.format("%s: %d", "Highest Score ", scoreBoard.get(0).score);
        canvas.drawText(yourScore, w2 / 2, (h3 - fontSize2 * 2) / 2 + fontSize2 - 5, textPaint);
        canvas.drawText(topScore, w2 / 2, (h3 - fontSize2 * 2) / 2 + 2 * fontSize2 + 5, textPaint);
        // draw line under score
        canvas.translate(0, h3);
        canvas.drawLine(0, 0, w2, 0, paint);
        // draw button
        Rect rect2 = new Rect();
        rect2.left = (w2 - buttonWidth) / 2;
        rect2.right = w2 - rect2.left;
        rect2.top = (h4 - buttonHeight) / 2;
        rect2.bottom = h4 - rect2.top;
        canvas.drawRect(rect2, paint);
        // draw continue button restart
        canvas.translate(0, rect2.top);
        canvas.drawText(operation, w2 / 2, (buttonHeight - fontSize2) / 2 + fontSize2, textPaint);
        continueRect = new Rect(rect2);
        continueRect.left = w1 + rect2.left;
        continueRect.right = continueRect.left + buttonWidth;
        continueRect.top = h1 + h2 + h3 + rect2.top;
        continueRect.bottom = continueRect.top + buttonHeight;
        // reset
        textPaint.setTextSize(originalFontSize);
        textPaint.setTextAlign(originalFontAlign);
        paint.setColor(originalColor);
        paint.setStyle(originalStyle);
    }

    /**
     * checks if user clicks restart button
     */
    private void clickRestartButton(float x, float y) {
        if (continueRect.contains((int) x, (int) y)) {
            restart();
        }
    }

    /**
     * resets game status, simplified version of init()
     */
    public void restart() {
        // destroy spirits
        sprites.addAll(playerBullets);
        sprites.addAll(readySprites);
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()) {
            Sprite s = iterator.next();
            s.destroy();
        }
        sprites.clear();
        playerBullets.clear();
        readySprites.clear();

        // reset
        tick = 0;
        score = 0;
        player = new PlayerPlane(images.get(0));
        boss = null;
        initializeGameStates(width, height);
        gameStatus = GAME_PLAYING;
        repaintInterval.start();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        GameView.score = score;
    }

    public void stopTimer() {
        this.repaintInterval.stop();
        this.updateInterval.stop();
    }

    /**
     * loads bitmap specified by android resource id
     */
    private Bitmap loadBitmap(int id) {
        Bitmap ret = BitmapFactory.decodeResource(getResources(), id);
        ret.prepareToDraw();
        return ret;
    }

    /**
     * write the score records to files
     */
    public void writeScoresToFile(){
        scoreBoard.writeToFile();
    }
}
