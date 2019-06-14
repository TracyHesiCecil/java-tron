package org.tron.core.services.http;

import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tron.api.GrpcAPI;
import org.tron.common.utils.ByteArray;
import org.tron.core.Wallet;

@Component
@Slf4j(topic = "API")
public class ScanNoteByOvkServlet extends HttpServlet {

  @Autowired
  private Wallet wallet;

  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      String input = request.getReader().lines()
          .collect(Collectors.joining(System.lineSeparator()));
      Util.checkBodySize(input);
      JSONObject jsonObject = JSONObject.parseObject(input);
      boolean visible = Util.getVisiblePost(input);

      long startNum =  Util.getJsonLongValue(jsonObject,"startNum", true);
      long endNum =  Util.getJsonLongValue(jsonObject,"endNum", true);

      String ovk = jsonObject.getString("ovk");

      GrpcAPI.DecryptNotes notes = wallet
          .scanNoteByOvk(startNum, endNum, ByteArray.fromHexString(ovk));

      response.getWriter()
          .println(JsonFormat.printToString(notes, visible));

    } catch (Exception e) {
      logger.debug("Exception: {}", e.getMessage());
      try {
        response.getWriter().println(Util.printErrorMsg(e));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
    }
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    try {
      boolean visible = Util.getVisible(request);
      long startNum = Long.parseLong(request.getParameter("startNum"));
      long endNum = Long.parseLong(request.getParameter("endNum"));
      String ovk = request.getParameter("ovk");

      GrpcAPI.DecryptNotes notes = wallet
          .scanNoteByOvk(startNum, endNum, ByteArray.fromHexString(ovk));

      response.getWriter()
          .println(JsonFormat.printToString(notes, visible));

    } catch (Exception e) {
      logger.debug("Exception: {}", e.getMessage());
      try {
        response.getWriter().println(Util.printErrorMsg(e));
      } catch (IOException ioe) {
        logger.debug("IOException: {}", ioe.getMessage());
      }
    }
  }
}
