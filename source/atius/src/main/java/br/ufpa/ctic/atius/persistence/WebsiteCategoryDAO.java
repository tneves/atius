package br.ufpa.ctic.atius.persistence;

import java.util.List;

import br.gov.frameworkdemoiselle.ldap.template.LDAPCrud;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.ufpa.ctic.atius.domain.WebsiteCategory;

@PersistenceController
public class WebsiteCategoryDAO extends LDAPCrud<WebsiteCategory, String> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public List<WebsiteCategory> findAll() {
		return getEntryManager().createQuery("objectClass=websiteCategory").getResultList();
	}
}
