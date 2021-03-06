package br.ufpa.ctic.atius.web.persistence;

import br.gov.frameworkdemoiselle.ldap.template.LDAPCrud;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.ufpa.ctic.atius.web.domain.WebsiteCategory;

@PersistenceController
public class WebsiteCategoryDAO extends LDAPCrud<WebsiteCategory, String> {

	private static final long serialVersionUID = 1L;

}
