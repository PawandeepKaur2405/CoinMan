package com.zappycode.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.media.MediaPlayer;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture dizzy;
	int manState = 0;
	int pause = 0;
	float gravity = 0.5f;
	float velocity = 0f;
	int manY = 0;
	Rectangle manRecangle;
	private Music music;
	private Music coinMusic;
	private Music runMusic;
	private Music overMusic;

	int score = 0;
	BitmapFont font;

	int gameState = 0;

	Random random;

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {		//used when the app is opened very first time.
		batch = new SpriteBatch();	//to draw everything to the screen
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		dizzy = new Texture("dizzy-1.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(8);

		 music = Gdx.audio.newMusic(Gdx.files.internal("jump.mp3"));
		coinMusic = Gdx.audio.newMusic(Gdx.files.internal("coins.mp3"));
		runMusic = Gdx.audio.newMusic(Gdx.files.internal("run.mp3"));
		overMusic = Gdx.audio.newMusic(Gdx.files.internal("over.mp3"));



	}

	public void makeCoin()		//used because we want to make multiple coins on screen
	{
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb()
	{
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {		//to repeat over and over till we stop the game
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1)
		{
			//Game is live

			runMusic.setLooping(true);
			runMusic.play();
			//BOMBS
			if(bombCount < 250)
			{
				bombCount++;
			}
			else
			{
				bombCount=0;
				makeBomb();
			}

			bombRectangles.clear();
			for(int i=0 ; i<bombXs.size();i++)
			{
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i , bombXs.get(i)-8);

				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//COINS
			if(coinCount < 100)
			{
				coinCount++;
			}
			else
			{
				coinCount=0;
				makeCoin();
			}

			coinRectangles.clear();
			for(int i=0 ; i<coinXs.size();i++)
			{
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i , coinXs.get(i)-4);

				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			if(Gdx.input.justTouched())
			{
				velocity = -10;
				music.play();
			}

			if(pause<6)
			{
				pause++;
			}
			else
			{
				pause = 0;
				if(manState <3)
				{
					manState++;
				}
				else
				{
					manState = 0;
				}
			}

			velocity = velocity + gravity;
			manY -= velocity;

			if(manY <= 0)
			{
				manY = 0;
			}

		}
		else if(gameState ==0)
		{
			//WAITING TO START
			if(Gdx.input.justTouched())
			{
				gameState=1;
			}
		}
		else if(gameState ==2)
		{
			//Game is over
			runMusic.stop();
			coinMusic.stop();
			overMusic.play();
			if(Gdx.input.justTouched())
			{
				gameState=1;
				overMusic.stop();
				manY = Gdx.graphics.getHeight()/2;
				score =0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount =0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount =0;
			}
		}


		if(gameState==2)
		{
			batch.draw(dizzy,Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY);
		}
		else
		{
			batch.draw(man[manState],Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY);
		}

		manRecangle = new Rectangle(Gdx.graphics.getWidth()/2 - man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight());

		for(int i=0 ; i<coinRectangles.size() ; i++)
		{
			if(Intersector.overlaps(manRecangle,coinRectangles.get(i)))
			{
				coinMusic.play();
				score++;
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		for(int i=0 ; i<bombRectangles.size() ; i++)
		{
			if(Intersector.overlaps(manRecangle,bombRectangles.get(i)))
			{
				Gdx.app.log("Bomb!"," Collision!");
				gameState =2;
			}
		}

		font.draw(batch,String.valueOf(score),100,150);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
