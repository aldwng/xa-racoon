package net.xa.racoon.config;

import java.io.Serializable;
import java.util.List;

/**
 * @author aldywang
 */

public class ConfigObject implements Serializable {

	private List<CorrectionObject> corrections;

	public List<CorrectionObject> getCorrections() {
		return corrections;
	}

	public void setCorrections(List<CorrectionObject> corrections) {
		this.corrections = corrections;
	}
}
