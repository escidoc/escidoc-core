package org.escidoc.core.business.domain.om.item;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotBlank;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

import org.escidoc.core.business.domain.base.ID;
import org.escidoc.core.business.domain.base.Pid;
import org.escidoc.core.business.domain.common.CommonPropertiesDO;
import org.escidoc.core.business.domain.common.LockInfoDO;
import org.escidoc.core.business.domain.common.StatusInfoDO;
import org.escidoc.core.business.domain.common.VersionInfoDO;
import org.escidoc.core.business.util.aspect.ValidationProfile;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 * @author Michael Hoppe (michael.hoppe@fiz-karlsruhe.de)
 */
@Guarded(checkInvariants = true, inspectInterfaces = true)
public class ItemPropertiesDO extends CommonPropertiesDO {

	private ID origin;

	@NotNull
	private ID context;

	@NotNull
	private ID contentModel;

	// xml: object-status, object-status-comment
	@NotNull(profiles = { ValidationProfile.EXISTS })
	private StatusInfoDO<ItemStatus> statusInfo;

	// Only show in latest version
	private LockInfoDO lockInfo;

	/**
	 * TODO: Discuss: URI instead of String? Migration? OR own Type PID xml name
	 * object-pid
	 */
	@NotBlank
	private Pid objectPid;

	// alles in PropertiesDo
	private VersionInfoDO currentVersionInfo;

	public ItemPropertiesDO(@AssertFieldConstraints final ID context,
			@AssertFieldConstraints final ID contentModel) {

		this.context = context;
		this.contentModel = contentModel;
	}

	public void setOrigin(@AssertFieldConstraints final ID origin) {
		this.origin = origin;
	}

	public void setStatusInfo(
			@AssertFieldConstraints final StatusInfoDO<ItemStatus> statusInfo) {
		this.statusInfo = statusInfo;
	}

	public void setLockInfo(@AssertFieldConstraints final LockInfoDO lockInfo) {
		this.lockInfo = lockInfo;
	}

	public void setObjectPid(@AssertFieldConstraints final Pid objectPid) {
		this.objectPid = objectPid;
	}

	public void setCurrentVersionInfo(
			@AssertFieldConstraints final VersionInfoDO currentVersionInfo) {
		this.currentVersionInfo = currentVersionInfo;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(ID context) {
		this.context = context;
	}

	/**
	 * @param contentModel
	 *            the contentModel to set
	 */
	public void setContentModel(ID contentModel) {
		this.contentModel = contentModel;
	}

	@AssertFieldConstraints
	public ID getOrigin() {
		return origin;
	}

	@AssertFieldConstraints
	public ID getContext() {
		return context;
	}

	@AssertFieldConstraints
	public ID getContentModel() {
		return contentModel;
	}

	@AssertFieldConstraints
	public StatusInfoDO<ItemStatus> getStatusInfo() {
		return statusInfo;
	}

	@AssertFieldConstraints
	public LockInfoDO getLockInfo() {
		return lockInfo;
	}

	@AssertFieldConstraints
	public Pid getObjectPid() {
		return objectPid;
	}

	@AssertFieldConstraints
	public VersionInfoDO getCurrentVersionInfo() {
		return currentVersionInfo;
	}

	// TODO: toString() etc.
}
