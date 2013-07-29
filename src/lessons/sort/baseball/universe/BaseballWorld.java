package lessons.sort.baseball.universe;

import javax.script.ScriptEngine;

import jlm.core.model.ProgrammingLanguage;
import jlm.core.ui.WorldView;
import jlm.core.utils.FileUtils;
import jlm.universe.EntityControlPanel;
import jlm.universe.World;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class BaseballWorld extends World {
	public static final int MIX_SORTED = 0;
	public static final int MIX_RANDOM = 1;
	public static final int MIX_NOBODY_HOME = 2;
	public static final int MIX_ALMOST_SORTED = 3;
	
	
	public static final int COLOR_HOLE = -1;

	private int[] field; // the bases which composed the field
	private int baseAmount,posAmount; // field dimensions
	private int holeBase,holePos; // The coordinate of the hole
	private BaseballMove lastMove; // the last move made on the field -- used for graphical purpose only
	private I18n i18n;

	/** Copy constructor used internally by JLM */
	public BaseballWorld(BaseballWorld world) {
		super(world);
		i18n = I18nFactory.getI18n(getClass(),"org.jlm.i18n.Messages",FileUtils.getLocale(), I18nFactory.FALLBACK);
	}


	/** Regular constructor used by exercises */
	public BaseballWorld(String name, int baseAmount, int positionAmount) {
		this(name,baseAmount,positionAmount,MIX_RANDOM);
	}

	public BaseballWorld(String name, int baseAmount, int posAmount, int mix) {
		super(name);
		i18n = I18nFactory.getI18n(getClass(),"org.jlm.i18n.Messages",FileUtils.getLocale(), I18nFactory.FALLBACK);

		// create the bases
		this.baseAmount = baseAmount;
		this.posAmount = posAmount;
		
		this.field = new int[baseAmount*posAmount];
		for (int base = 0 ; base < baseAmount ; base++)
			for (int pos = 0; pos < posAmount; pos++)
				setPlayerColor(base, pos, base);
		setPlayerColor(baseAmount-1, 0, COLOR_HOLE);
		

		lastMove = null;
		
		if (mix == MIX_RANDOM) {
			for (int base = 0 ; base<getBasesAmount();base++)
				for (int pos = 0 ; pos<getPositionsAmount();pos++)
					swap(base, pos, (int) (Math.random()*getBasesAmount()), (int) (Math.random()*getPositionsAmount()));
		
		} else if (mix == MIX_NOBODY_HOME) {
			// Ensure that nobody's home once it's mixed. 
			//   We tested that no situation of 4 bases with that condition exposes the bug of the naive algorithm
			//   We tested it by generating all situations, actually.
			boolean swapped;
			do {
				swapped = false;
				for (int base = 0 ; base<getBasesAmount();base++)
					for (int pos = 0 ; pos<getPositionsAmount();pos++)
						if (getPlayerColor(base, pos) == base) {
							swapped = true;
							int newBase;
							do {
								newBase = (int) (Math.random()*getBasesAmount());
							} while (newBase == base);
							int newPos = (int) (Math.random()*getPositionsAmount());
							swap(base, pos, newBase, newPos);							
						}
			} while (swapped);
		} else if (mix == MIX_SORTED) {
			/* nothing to do here */
		} else if (mix == MIX_ALMOST_SORTED) {
			/* Expose the bug of the naive algorithm */
			swap(0,0,  2,0);
		} else {
			throw new IllegalArgumentException("The mix paramter must be one of the provided constants, not "+mix);
		}

		// Add an entity
		new BaseballEntity("Baseball Player",this);
		
		// Cache the hole position 
		for ( int base = 0 ; base < getBasesAmount(); base++)
			for ( int pos = 0 ; pos < getPositionsAmount(); pos++)
				if ( getPlayerColor(base,pos)== COLOR_HOLE) {
					holeBase = base;
					holePos = pos;
					return;
				}
		
	}


	/**
	 * Returns a textual description of the differences between the caller and world
	 * @param o the world with which you want to compare your world
	 */
	public String diffTo(World o) {
		if (o == null || !(o instanceof BaseballWorld))
			return i18n.tr("This is not a baseball world :-(");

		BaseballWorld other = (BaseballWorld) o;
		if (getBasesAmount() != other.getBasesAmount())
			return i18n.tr("Differing amount of bases: {0} vs {1}",getBasesAmount(),other.getBasesAmount());

		if (getPositionsAmount() != ((BaseballWorld) o).getPositionsAmount())
			return i18n.tr("Differing amount of players: {0} vs {1}", getPositionsAmount(), other.getPositionsAmount());

		StringBuffer sb = new StringBuffer();
		for (int base = 0; base< baseAmount; base++)
			for (int pos=0; pos<posAmount; pos++)
				if (getPlayerColor(base, pos) != other.getPlayerColor(base, pos))
					sb.append(i18n.tr("Player at base {0}, pos {1} differs: {2} vs {3}\n",base,pos,getPlayerColor(base, pos), other.getPlayerColor(base, pos)));

		return sb.toString();
	}

	public boolean equals(Object other) {
		if (other == null || !(other instanceof BaseballWorld))
			return false;

		BaseballWorld otherField = (BaseballWorld) other;
		if (   this.holeBase != otherField.holeBase
				|| this.holePos != otherField.holePos
				|| this.getBasesAmount() != otherField.getBasesAmount()
				|| this.getPositionsAmount() != otherField.getPositionsAmount())

			return false;

		for (int base = 0; base< baseAmount; base++)
			for (int pos=0; pos<posAmount; pos++)
				if (getPlayerColor(base, pos) != otherField.getPlayerColor(base, pos))
					return false;

		return true;
	}

	/** Ensures that the provided script engine can run scripts in the specified programming language */
	@Override
	public void setupBindings(ProgrammingLanguage lang, ScriptEngine e) {
		throw new RuntimeException("No binding of BaseballWorld for "+lang);
	}

	/** Returns a component able to display the world */
	public WorldView getView() {
		return new BaseballWorldView(this);
	}
	
	BaseballMovePanel panel = null; 
	/** Returns a panel allowing to interact dynamically with the world */
	@Override
	public EntityControlPanel getEntityControlPanel() {
		if (panel == null)
			panel = new BaseballMovePanel();
		return panel;
	}
	/** Passes the mouse selection from view to the control panel */ 
	public void setPlayer(int base, int pos) {
		panel.setPlayer(base, pos);
	}
	/** Passes the mouse action from the view to the control panel */
	public void doMove() {
		panel.doMove();
	}

	/** 
	 * Reset the state of the current world to the one passed in argument
	 * @param the world which must be the new start of your current world
	 */
	public void reset(World world) {
		super.reset(world);		

		BaseballWorld other = (BaseballWorld) world;
		
		lastMove = other.lastMove;
		
		holeBase = other.holeBase;
		holePos = other.holePos;

		baseAmount = other.baseAmount;
		posAmount = other.posAmount;
		field= new int[other.baseAmount*other.posAmount];
		for (int base=0; base<baseAmount; base++)
			for (int pos=0; pos<posAmount; pos++)
				setPlayerColor(base, pos, other.getPlayerColor(base, pos));
	}

	/** Returns a string representation of the world */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BaseballWorld "+getName()+": {");

		for (int base = 0 ; base < baseAmount ; base++) {
			if (base!=0)
				sb.append(" , ");
			for (int pos = 0 ; pos < posAmount ; pos++) {
				if (pos!=0)
					sb.append(",");
				sb.append(getPlayerColor(base,pos));
			}
		}
		sb.append("}");
		return sb.toString();
	}

	/** Returns the number of bases on your field */
	public int getBasesAmount() {
		return baseAmount;
	}
	/** Returns the amount of players per base on this field */
	public int getPositionsAmount() {
		return posAmount;
	}

	/**
	 * Returns the color of the player in base baseIndex at position playerLocation
	 * @param base the index of the base we are looking for
	 * @param pos  the position within that base (between 0 and getLocationsAmount()-1 )
	 */
	public int getPlayerColor(int base, int pos)  {
		return field[base*posAmount+pos];
	}
	/**
	 * Sets the color of the player in the specified base at the specified position to the specified value 
	 * @param base the index of the base we are looking for
	 * @param pos  the position within that base (between 0 and getLocationsAmount()-1 )
	 * @param color the new value
	 */
	public void setPlayerColor(int base, int pos, int color)  {
		field[base*posAmount+pos] = color;
	}

	/** Returns the index of the base where is hole is located */
	public int getHoleBase() {
		return this.holeBase;
	}

	/** Returns the position in the base where is hole is located */
	public int getHolePosition(){
		return this.holePos;
	}

	/** Returns the last move made on the field */
	public BaseballMove getLastMove() {
		return lastMove;
	}

	/** Returns if every player of the field is on the right base */
	public boolean isSorted() {
		for (int base=0; base<baseAmount; base++)
			for (int pos=0; pos<posAmount; pos++)
				if (base==baseAmount-1) {// last base, may contain the hole
					if (   getPlayerColor(base, pos) != COLOR_HOLE 
					    && getPlayerColor(base, pos) != base)
						return false;
				} else if (getPlayerColor(base, pos) != base)
					return false;
		return true;
	}
	/** Returns if every player of the specified base is on the right base */
	public boolean isBaseSorted(int base) {
		for (int pos=0;pos<posAmount;pos++)
			if (base==baseAmount-1) // last base, may contain the hole
				if (   getPlayerColor(base, pos) != COLOR_HOLE 
				    && getPlayerColor(base, pos) != base)
					return false;
			else if (getPlayerColor(base, pos) != base)
				return false;
		
		return true;
	}

	/**
	 * Moves the specified player into the hole
	 * @throws IllegalArgumentException in case baseSrc is not near the hole
	 */
	public void move(int base, int position) {
		if ( base >= this.getBasesAmount() || base < 0)
			throw new IllegalArgumentException(i18n.tr("Cannot move from base {0} since it's not between 0 and {1}",base,(getBasesAmount()-1)));

		if ( position < 0 || position > this.getPositionsAmount()-1 )
			throw new IllegalArgumentException(i18n.tr("Cannot move from position {0} since it's not between 0 and {1})",position,(getPositionsAmount()-1)));

		// must work only if the bases are next to each other
		if (	(holeBase != base+1)
			 && (holeBase != base-1)
			 && (holeBase != 0                  || base != getBasesAmount()-1 )
			 && (holeBase != getBasesAmount()-1 || base != 0 )
			 && (holeBase != base ) )
			
			throw new IllegalArgumentException("The player "+position+" from base "+base+" is too far from the hole (at base "+holeBase+") to reach it in one move");

		// All clear. Proceed.
		lastMove  = new BaseballMove(base, position, holeBase, holePos, getPlayerColor(base, position));
		swap(base, position, holeBase,holePos);
		holeBase = base;
		holePos = position;
	}


	/**
	 * Swap two players (no validity check is enforced)
	 * @param baseSrc : the index of the source base
	 * @param posSrc : the position of the player that you want to move from the source base
	 * @param baseDst : the index of the destination base
	 * @param posDst : the position of the player that you want to move from the destination base
	 */
	private void swap(int baseSrc, int posSrc, int baseDst, int posDst) {
		int flyingMan = getPlayerColor(baseSrc,posSrc);

		setPlayerColor(baseSrc, posSrc,   getPlayerColor(baseDst,posDst));
		setPlayerColor(baseDst, posDst,   flyingMan);
	}
}
