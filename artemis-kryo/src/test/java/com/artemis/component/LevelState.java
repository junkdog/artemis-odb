package com.artemis.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.PooledWeaver;

@PooledWeaver
public class LevelState extends Component {
	public int score;

	@EntityId
	public int starId1;
	@EntityId
	public int starId2;
	@EntityId
	public int starId3;

	@Override
	public String toString() {
		return "LevelState{" +
				"score=" + score +
				", starId1=" + starId1 +
				", starId2=" + starId2 +
				", starId3=" + starId3 +
				'}';
	}
}
