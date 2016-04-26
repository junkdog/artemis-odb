package com.artemis.link;

public interface LinkListener {
	void onLinkEstablished(int sourceId, int targetId);
	void onLinkKilled(int sourceId);
	void onTargetDead(int sourceId, int deadTargetId);
	void onTargetChanged(int sourceId, int oldTargetId);
//	void onTargetInvalid(int sourceId);
}
