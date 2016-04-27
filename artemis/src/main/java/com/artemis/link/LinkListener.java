package com.artemis.link;

/**
 * Callbacks for links between entities.
 */
public interface LinkListener {

	/**
	 * Established connection between <code>sourceId:ComponentType:Field</code>
	 * and <code>targetId</code>.
	 *
	 * @param sourceId entity owning component.
	 * @param targetId a valid entity id.
	 */
	void onLinkEstablished(int sourceId, int targetId);

	/**
	 * Deletion of source entity or its component.
	 *
	 * @param sourceId entity owning component.
	 */
	void onLinkKilled(int sourceId);

	/**
	 * Target entity dead.
	 * 
	 * @param sourceId entity owning component.
	 * @param deadTargetId a valid entity id.
	 */
	void onTargetDead(int sourceId, int deadTargetId);

	/**
	 * Target entity has changed.
	 *
	 * @param sourceId entity owning component.
	 * @param targetId a valid entity id.
	 * @param oldTargetId previous entity id.
	 */
	void onTargetChanged(int sourceId, int targetId, int oldTargetId);
}
