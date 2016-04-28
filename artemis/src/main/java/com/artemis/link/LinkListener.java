package com.artemis.link;

/**
 * Callbacks for links between entities. Implementations undergo dependency-
 * injection when registered with {@link EntityLinkManager}
 *
 * @see EntityLinkManager#register(Class, LinkListener)
 * @see EntityLinkManager#register(Class, String, LinkListener)
 */
public interface LinkListener {

	/**
	 * Established connection between <code>sourceId:ComponentType:Field</code>
	 * and <code>targetId</code>.
	 *
	 * @param sourceId entity owning component.
	 * @param targetId a valid entity id, or -1 if source links via IntBag or Bag-of-Entity.
	 */
	void onLinkEstablished(int sourceId, int targetId);

	/**
	 * Deletion of source entity or its component.
	 *
	 * @param sourceId entity owning component.
	 * @param targetId appointed child, or -1 if source links via IntBag or Bag-of-Entity.
	 */
	void onLinkKilled(int sourceId, int targetId);

	/**
	 * Target entity dead.
	 * 
	 * @param sourceId entity owning component.
	 * @param deadTargetId a valid entity id.
	 */
	void onTargetDead(int sourceId, int deadTargetId);

	/**
	 * Target entity has changed. Never called if source links via IntBag or Bag-of-Entity.
	 *
	 * @param sourceId entity owning component.
	 * @param targetId a valid entity id.
	 * @param oldTargetId previous entity id.
	 */
	void onTargetChanged(int sourceId, int targetId, int oldTargetId);
}
