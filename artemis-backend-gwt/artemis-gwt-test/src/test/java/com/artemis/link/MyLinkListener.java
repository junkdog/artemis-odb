package com.artemis.link;

import junit.framework.Assert;

public class MyLinkListener implements LinkListener {
	private final int e;
	private final int otherA;
	private final int otherB;

	public MyLinkListener(int e, int otherA, int otherB) {
		this.e = e;
		this.otherA = otherA;
		this.otherB = otherB;
	}

	@Override
	public void onLinkEstablished(int sourceId, int targetId) {
		Assert.assertEquals(sourceId, e);
		Assert.assertEquals(targetId, otherA);
	}

	@Override
	public void onLinkKilled(int sourceId, int targetId) {
		Assert.assertEquals(sourceId, e);
		Assert.assertEquals(targetId, otherA);
	}

	@Override
	public void onTargetDead(int sourceId, int deadTargetId) {
		Assert.assertEquals(sourceId, e);
		Assert.assertEquals(deadTargetId, otherB);
	}

	@Override
	public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
		Assert.assertEquals(sourceId, e);
		Assert.assertEquals(targetId, otherB);
		Assert.assertEquals(oldTargetId, otherA);
	}
}
