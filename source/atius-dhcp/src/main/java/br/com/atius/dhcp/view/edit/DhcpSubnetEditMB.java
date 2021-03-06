package br.com.atius.dhcp.view.edit;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.net.util.SubnetUtils;

import br.com.atius.dhcp.business.DhcpSubnetBC;
import br.com.atius.dhcp.domain.DhcpSubnet;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.contrib.Faces;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@ViewController
public class DhcpSubnetEditMB extends AbstractEditPageBean<DhcpSubnet, String> {

	private static final long serialVersionUID = 1L;

	@Inject
	private DhcpSubnetBC bc;

	@Override
	public void editBean() {
		super.editBean();
		getBean().setDhcpNetMask("24");
	}

	@Override
	public void editBean(DhcpSubnet bean) {
		super.editBean(bean);
		bc.selectDhcpSubnet(bean);
	}

	@Override
	public String insert() {
		if (validate()) {
			try {
				getBean().setParentDN(bc.getDhcpSharedNetworkDN());
				getBean().set();
				bc.insert(getBean());
				Faces.addI18nMessage("atius.dhcp.subnet.insert.success", getBean().getCn());
			} catch (RuntimeException e) {
				Faces.validationFailed();
				Faces.addI18nMessage("atius.dhcp.subnet.insert.failed", SeverityType.ERROR);
			}
		}
		return null;
	}

	@Override
	public String update() {
		if (validate()) {
			try {
				getBean().set();
				bc.update(getBean());
				Faces.addI18nMessage("atius.dhcp.subnet.update.success", getBean().getCn());
			} catch (RuntimeException e) {
				Faces.validationFailed();
				Faces.addI18nMessage("atius.dhcp.subnet.update.failed", SeverityType.ERROR);
			}
		}
		return null;
	}

	private boolean validate() {
		boolean validate = true;
		SubnetUtils subnet;

		try {
			subnet = new SubnetUtils(getBean().getCn() + "/" + getBean().getDhcpNetMask());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("atius.dhcp.validation.networkaddress.failed", SeverityType.ERROR);
			return false;
		}

		if (Strings.isNotEmpty(getBean().getDhcpGateway()))
			try {
				if (!subnet.getInfo().isInRange(getBean().getDhcpGateway()))
					throw new IllegalArgumentException("Gateway is not in subnet");
			} catch (RuntimeException e) {
				Faces.validationFailed();
				Faces.addI18nMessage("atius.dhcp.validation.gateway.failed", SeverityType.ERROR);
				validate = false;
			}

		getBean().setDhcpRange();
		if (Strings.isNotBlank(getBean().getDhcpRangeFirst()) || Strings.isNotBlank(getBean().getDhcpRangeLast())) {
			try {
				if (!subnet.getInfo().isInRange(getBean().getDhcpRangeFirst()) || !subnet.getInfo().isInRange(getBean().getDhcpRangeLast()))
					throw new IllegalArgumentException("Range is not in subnet");
			} catch (RuntimeException e) {
				Faces.validationFailed();
				Faces.addI18nMessage("atius.dhcp.validation.range.failed", SeverityType.ERROR, getBean().getCn() + "/"
						+ getBean().getDhcpNetMask());
				validate = false;
			}
		}
		return validate;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getCn());
			Faces.addI18nMessage("atius.dhcp.subnet.delete.success", getBean().getInfo());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("atius.dhcp.subnet.delete.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	protected DhcpSubnet load(String id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("atius.dhcp.subnet.load.failed", SeverityType.ERROR);
		}
		return new DhcpSubnet();
	}

	public List<String> getNetmasks() {
		List<String> netmasks = new ArrayList<String>();
		for (int i = 32; i > -1; i--)
			netmasks.add(String.valueOf(i));
		return netmasks;
	}

}
