package com.pnimac.webcrawler.response;

import com.fasterxml.jackson.annotation.JsonInclude;

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
public class ScanResponse {
	private String message;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Object reason;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Object data;
	
	private int code;
}