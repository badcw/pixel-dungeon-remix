package com.nyrds.pixeldungeon.levels.objects;

import com.nyrds.android.util.Util;
import com.nyrds.pixeldungeon.levels.objects.sprites.LevelObjectSprite;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.hero.Hero;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class LevelObject implements Bundlable, Presser {

	private static final String POS = "pos";
	private              int    pos = -1;
	
	public LevelObjectSprite sprite;

	public LevelObject(int pos) {
		this.pos = pos;
	}

	abstract public int image();

	abstract void setupFromJson(Level level, JSONObject obj) throws JSONException;

	public boolean interact(Hero hero ) {return true;}
	public boolean stepOn(Char hero) {return true;}

	protected void remove() {
		Dungeon.level.remove(this);
		sprite.kill();
	}

	public void burn() {}
	public void freeze() {}
	public void poison(){}
	public void bump() {}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		setPos(bundle.getInt( POS ));
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, getPos());
	}
	
	public boolean dontPack() {
		return false;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		if(sprite!=null) {
			sprite.move(this.pos,pos);
			Dungeon.level.levelObjectMoved(this);
		}

		this.pos = pos;
	}

	public abstract String desc();

	public abstract String name();

	public String texture(){
		return "levelObjects/objects.png";
	}

	public boolean pushable(Char hero) {
		return false;
	}

	public boolean push(Char hero){
		Level level = Dungeon.level;
		int hx = level.cellX(hero.getPos());
		int hy = level.cellY(hero.getPos());

		int x = level.cellX(getPos());
		int y = level.cellY(getPos());

		int dx = x - hx;
		int dy = y - hy;

		if (dx * dy != 0) {
			return false;
		}

		int nextCell = level.cell(x + Util.signum(dx), y + Util.signum(dy));

		if (!level.cellValid(nextCell)) {
			return false;
		}

		if (level.solid[nextCell] || level.getLevelObject(nextCell) != null) {
			return false;
		} else {
			setPos(nextCell);

			if (level.objectPress(nextCell, this)) {
				level.levelObjectMoved(this);
			}
		}

		return true;
	}

	public void fall() {
		if(sprite != null) {
			sprite.fall();
			Dungeon.level.remove(this);
		}
	}

	@Override
	public boolean affectLevelObjects() {
		return false;
	}

	public int getSpriteXS() {
		return 16;
	}

	public int getSpriteYS() {
		return 16;
	}
}
