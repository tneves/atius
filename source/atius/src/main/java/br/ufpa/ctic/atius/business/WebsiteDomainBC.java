package br.ufpa.ctic.atius.business;

import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.message.DefaultMessage;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.template.DelegateCrud;
import br.gov.frameworkdemoiselle.util.Faces;
import br.gov.frameworkdemoiselle.util.MenuContext;
import br.gov.frameworkdemoiselle.util.Strings;
import br.ufpa.ctic.atius.domain.DomainContainer;
import br.ufpa.ctic.atius.domain.InetOrgPerson;
import br.ufpa.ctic.atius.domain.WebsiteCategory;
import br.ufpa.ctic.atius.domain.WebsiteDomain;
import br.ufpa.ctic.atius.domain.WebsiteProfile;
import br.ufpa.ctic.atius.persistence.WebsiteDomainDAO;

@BusinessController
public class WebsiteDomainBC extends DelegateCrud<WebsiteDomain, String, WebsiteDomainDAO> {

	private static final long serialVersionUID = 1L;

	@Inject
	private InetOrgPersonBC inetOrgPersonBC;

	@Inject
	private WebsiteCategoryBC websiteCategoryBC;

	@Inject
	private WebsiteProfileBC websiteProfileBC;

	@Inject
	private DomainContainerBC domainContainerBC;

	@Inject
	private MenuContext menuContext;

	public String getSelectedMenu() {
		return menuContext.getSelected("MenuSites");
	}

	public void selectMenu(String itemName) {
		menuContext.select("MenuSites", itemName);
	}

	public List<WebsiteCategory> getOrderedWebsiteCategories() {
		return websiteCategoryBC.getOrderedWebsiteCategories();
	}

	public List<String> getWebsiteProfiles() {
		return websiteProfileBC.getNames();
	}

	public void insert(WebsiteDomain websiteDomain) {
		WebsiteProfile websiteProfile = websiteProfileBC.load(websiteDomain.getWebsiteProfile());
		if (Strings.isBlank(websiteProfile.getWebserverName())) {
			Faces.addMessage(new DefaultMessage("Não foi possível identificar o webserver do tipo " + websiteDomain.getWebsiteProfile()));
			Faces.validationFailed();
			return;
		}
		DomainContainer domainContainer = domainContainerBC.getNextFreeUidNumber(websiteProfile.getWebserverName());
		if (domainContainer == null) {
			Faces.addMessage(new DefaultMessage("O uidNumber sugerido não esta disponível"));
			Faces.validationFailed();
			return;
		}
		websiteDomain.setUidNumber(String.valueOf(domainContainer.getNextUidNumber()));
		websiteDomain.setDn("serverName=" + websiteDomain.getServerName() + "," + domainContainer.getDn());
		getDelegate().insert(websiteDomain);
	}

	public void disable(WebsiteDomain websiteDomain) {
		websiteDomain.setAvailability("disabled");
		update(websiteDomain);
	}

	public void enable(WebsiteDomain websiteDomain) {
		websiteDomain.setAvailability("enabled");
		update(websiteDomain);
	}

	public boolean domainAvailable(String serverName) {
		if (serverName != null && serverName.length() > 8) {
			WebsiteDomain websiteDomain = getDelegate().load(serverName);
			if (websiteDomain == null || websiteDomain.getServerName() == null)
				return true;
		}
		return false;
	}

	public List<InetOrgPerson> findPerson(String search) {
		return inetOrgPersonBC.findPerson(search);
	}

	public List<WebsiteDomain> find(String search) {
		WebsiteDomain websiteDomain = new WebsiteDomain(true);
		websiteDomain.setCn(search);
		websiteDomain.setServerName(search);
		InetOrgPerson inetOrgPerson = new InetOrgPerson();
		inetOrgPerson.setMail(search);
		websiteDomain.setAdminId(inetOrgPerson);
		websiteDomain.setOwnerId(inetOrgPerson);
		return findByExample(websiteDomain, false, 0);
	}

	public List<WebsiteDomain> findByCategory(String category) {
		if ("Todos".equals(category))
			return getDelegate().findAll();
		WebsiteDomain websiteDomain = new WebsiteDomain(true);
		websiteDomain.setWebsiteCategory(category);
		return findByExample(websiteDomain, true, 0);
	}

	public List<WebsiteDomain> findByCategory(String category, String search) {
		if (Strings.isBlank(search))
			return findByCategory(category);
		if (Strings.isBlank(category) || "Todos".equals(category))
			return find(search);
		return getDelegate().findByCategory(category, search);
	}
}
