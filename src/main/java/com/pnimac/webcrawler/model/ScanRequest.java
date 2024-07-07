package com.pnimac.webcrawler.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanRequest {

	@NotNull
	@NotBlank
	private String url;

	private Integer breakPoint;

	private Boolean domainOnly;
}
