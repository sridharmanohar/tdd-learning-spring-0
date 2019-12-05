package com.tdd.spring;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMetroSuper {

  enum HTTPSTATUS {
    OK(200), BADREQUEST(400);
    protected int value;

    private HTTPSTATUS(int value) {
      this.value = value;
    }
  }

  enum HTTPREQUEST {
    GET, POST
  }

  enum ACTION {
    GETMETROS("/metro"), PROPOSEMETRO("/proposeMetro/");
    protected String value;

    private ACTION(String value) {
      this.value = value;
    }
  }

  enum METRO {
    HYDERABAD("Hyderabad"), BENGALURU("Bengaluru"), CHENNAI("Chennai"), PUNE("Pune"),
    DELHI("Delhi"), MUMBAI("Mumbai"), KOLKATA("Kolkata"), CHANDIGARH("Chandigarh");
    protected String value;

    private METRO(String name) {
      this.value = name;
    }

  }

  enum METROSTATUS {
    CONFIRMED("confirmed"), PROPOSED("proposed");
    protected String value;

    private METROSTATUS(String name) {
      this.value = name;
    }

  }

  private final MockMvc mockMvc;
  private final ObjectMapper objectMapper;

  public TestMetroSuper(MockMvc mockMvc, ObjectMapper objectMapper) {
    super();
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  /**
   * converting the supplied mutli-dimensional array, containing metro name and
   * status is converted into a list of Metro objects.
   * 
   * @param input_array
   * @return
   */
  private List<Metro> getInputMetroList(String[][] input_array) {
    List<Metro> metroList = new ArrayList<>();
    for (int i = 0; i < input_array.length; i++) {
      for (int j = 0; j < 1; j++) {
        metroList.add(new Metro(input_array[i][j], input_array[i][j + 1]));
      }
    }
    return metroList;
  }

  
  private List<Metro> convertJSONResponseStringToList(String jsonresponseString)
      throws JsonMappingException, JsonProcessingException {
    return this.objectMapper.readValue(jsonresponseString, new TypeReference<List<Metro>>() {
    });
  }

  
  private String getJSONResponseString(String action, String path) throws Exception {
    String responseString = action.equalsIgnoreCase("get")
        ? mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse()
            .getContentAsString()
        : mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse()
            .getContentAsString();

    return responseString;
  }
  
  
  protected void mockThisCall(List<Metro> given, String[][] input_array) {
    Mockito.when(given).thenReturn(getInputMetroList(input_array));
  }

  
  protected long verifyIfMetroList__contains__suppliedNameandStatus(String metroName,
      String metroStatus, List<Metro> metroList) {
    long count = metroList.stream().filter(m -> {
      return m.getName().equalsIgnoreCase(metroName) ? m.getStatus().equalsIgnoreCase(metroStatus)
          : false;
    }).count();

    return count;
  }

  
  protected int getStatusCode(String action, String path) throws Exception {
    int status_code = action.equalsIgnoreCase("get")
        ? mockMvc.perform(MockMvcRequestBuilders.get(path)).andReturn().getResponse().getStatus()
        : mockMvc.perform(MockMvcRequestBuilders.post(path)).andReturn().getResponse().getStatus();

    return status_code;
  }

  
  protected List<Metro> getMetroList(String action, String path)
      throws JsonMappingException, JsonProcessingException, Exception {
    return convertJSONResponseStringToList(getJSONResponseString(action, path));
  }

  
  protected int getMetroListSize(String requestType, String action)
      throws JsonMappingException, JsonProcessingException, Exception {
    return getMetroList(requestType, action).size();
  }

}