package br.ufpa.ctic.atius.view.edit;

import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.message.DefaultMessage;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.util.Faces;
import br.ufpa.ctic.atius.business.WebsiteDomainBC;
import br.ufpa.ctic.atius.domain.InetOrgPerson;
import br.ufpa.ctic.atius.domain.WebsiteDomain;

@ViewController
public class WebsiteDomainEditMB extends AbstractEditPageBean<WebsiteDomain, String> {

	private static final long serialVersionUID = 1L;

	@Inject
	private WebsiteDomainBC bc;

	public String delete() {
		bc.delete(getBean().getServerName());
		Faces.addMessage(new DefaultMessage("Domínio " + getBean().getServerName() + " excluído com sucesso."));
		return null;
	}

	public String disable() {
		// bc.disable(getBean().getServerName());
		Faces.addMessage(new DefaultMessage("Domínio " + getBean().getServerName() + " desabilitado com sucesso."));
		return null;
	}

	public String insert() {
		if (!bc.domainAvailable(getBean().getServerName())) {
			Faces.addMessage("sites", new DefaultMessage("Domínio indisponível, escolha outro."));
			Faces.validationFailed();
			return null;
		}
		bc.insert(getBean());
		return null;
	}

	public String update() {
		System.out.println("==========> update()");
		// bc.update(getBean());
		return null;
	}

	public List<InetOrgPerson> findPerson(String search) {
		return bc.findPerson(search);
	}

	public String domainAvailable() {
		if (bc.domainAvailable(getBean().getServerName()))
			Faces.addMessage(new DefaultMessage("O domínio " + getBean().getServerName() + " está disponível"));
		else {
			Faces.addMessage(new DefaultMessage("Domínio indisponível, escolha outro."));
			Faces.validationFailed();
		}
		return null;
	}

	@Override
	protected WebsiteDomain loadBean(String serverName) {
		System.out.println("===> handleLoad()");
		// TODO Auto-generated method stub
		return bc.load(serverName);
	}

}
