package dev.learninggame.entities.creatures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import dev.learninggame.Handler;
import dev.learninggame.entities.Bomb;
import dev.learninggame.entities.Entity;
import dev.learninggame.gfx.Animation;
import dev.learninggame.gfx.Assets;
import dev.learninggame.tiles.Tile;

public class Player extends Creature implements Runnable{
	
	//Atributos
	private int maxBombs;
	private int nOfBombs;
	
	//Animations
	private Animation animUp;
	private Animation animDown;
	private Animation animLeft;
	private Animation animRight;
	private long tempoInicio;
	private long tempoFinal;
	
	@Override
	public void run() {
		System.out.println("player iniciado");
	}
	
	public Player(Handler handler, float x, float y) {	
		super(handler, x, y, Creature.DEFAULT_CREATURE_WIDTH, Creature.DEFAULT_CREATURE_HEIGHT);
		
		maxBombs = 3;
		
		//ajuste da posicao da hitbox
		bounds.x = 34;
		bounds.y = 38;
		//largura e comprimento da hitbox
		bounds.width = 30;
		bounds.height = 45;
		
		//Animations
		animUp = new Animation(180, Assets.player_up);
		animDown = new Animation(180, Assets.player_down);
		animLeft = new Animation(150, Assets.player_left);
		animRight = new Animation(150, Assets.player_right);
		
		//Contagem do tempo
		tempoInicio = 0;
		tempoFinal = System.currentTimeMillis();
	}

	@Override
	public void tick() {
		//Animations
		animUp.tick();
		animDown.tick();
		animRight.tick();
		animLeft.tick();
		
		//Tempo
		tempoFinal = System.currentTimeMillis();
		
		//Movement
		getInput();
		move();
		//handler.getGameCamera().centerOnEntity(this);
		//Attack
		checkAttacks();
		
	}
	
	//Melee Attacks
	private void checkAttacks() {
		Rectangle cb = getCollisionBounds(0, 0);
		Rectangle ar = new Rectangle();
		int arSize = 20;
		ar.width = arSize;
		ar.height = arSize;
		
		if(handler.getKeyManager().bomb) {
			ar.x = cb.x + cb.width / 2 - arSize / 2;
			ar.y = cb.y - arSize;
		}else
			return;
		
		for(Entity e : handler.getWorld().getEntityManager().getEntities()) {
			if(e.equals(this)) 
				continue;
			if(e.getCollisionBounds(0, 0).intersects(ar)) {
				e.hurt(1);
				return;
			}
			
		}
		
	}
	
	public void die() {
		System.out.println("U lose");
	}
	
	private void getInput() {
		xMove = 0; //variaveis declaradas na classe Creature
		yMove = 0;
		
		if(handler.getKeyManager().up)
			yMove = -speed;
		if(handler.getKeyManager().down)
			yMove = speed;
		if(handler.getKeyManager().left)
			xMove = -speed;
		if(handler.getKeyManager().right)
			xMove = speed;
		if(handler.getKeyManager().bomb) {
			installBomb();
		}
			
		
		/*else if(numero == 2) { 2 player
			if(handler.getKeyManager().up2)
				yMove = -speed;
			if(handler.getKeyManager().down2)
				yMove = speed;
			if(game.getKeyManager().left2)
				xMove = -speed;
			if(game.getKeyManager().right2)
				xMove = speed;
		}else {
			return;
		}*/
		
	}
	
	public void installBomb() {
		if(!handler.getWorld().hasBomb(getCurrentTileX(), getCurrentTileY()) 
				&& nOfBombs < maxBombs) {
			Bomb bomba = new Bomb(handler, (int)x, (int)y);
			handler.getWorld().getEntityManager().addBomb(bomba);
			System.out.println("Bomba plantada");
			handler.getWorld().setCoordinates(getCurrentTileX(), getCurrentTileY(), true);
			nOfBombs++;
		}
	}
	
	@Override
	public void render(Graphics g) {

		g.drawImage(getCurrentAnimation(), (int)(x), (int)(y), width, height, null);
		
		/*g.setColor(Color.red); // Testar hit box
		g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()), 
				(int) (y + bounds.y - handler.getGameCamera().getyOffset()), bounds.width, bounds.height);*/
	}

	private BufferedImage getCurrentAnimation() {
		if(xMove > 0)
			return animRight.getCurrentFrame();
		if(xMove < 0)
			return animLeft.getCurrentFrame();
		if(yMove > 0)
			return animDown.getCurrentFrame();
		if(yMove < 0)
			return animUp.getCurrentFrame();
		return Assets.player;
	}

	public int getnOfBombs() {
		return nOfBombs;
	}

	public void addnOfBombs() {
		this.nOfBombs++;
	}
	
	public int getMaxBombs() {
		return maxBombs;
	}

	private int getCurrentTileX() {
		for(int i = 0; i < (handler.getHeight()/Tile.TILEHEIGHT) + (Tile.TILEHEIGHT*2); i++) {
			if(x <= Tile.TILEHEIGHT*i) {
				return i;
			}
		}
		return 0;
	}
	
	private int getCurrentTileY() {
		for(int i = 0; i < handler.getHeight()/Tile.TILEHEIGHT + (Tile.TILEWIDTH*2); i++) {
			if(y <= Tile.TILEHEIGHT*i) {
				return i;
			}
		}
		return 0;
	}

}
