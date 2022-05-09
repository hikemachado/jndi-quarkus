package io.jndi.report.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

@WebServlet("/report")
public class ReportProcessorServlet extends HttpServlet{

    public ReportProcessorServlet(){}

    public void init(){
      ClassicEngineBoot.getInstance().start();
    }

   
  protected void doGet( final HttpServletRequest req, final HttpServletResponse resp ) throws ServletException, IOException {
    if(req.getParameter("report_file") == null ){
      System.out.println("required parameter report_file is missing.");
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "required parameter report_file is missing.");
    }else{
      generateReport( req, resp );
    }
  }

  protected void doPost( final HttpServletRequest req, final HttpServletResponse resp ) throws ServletException, IOException {
   //it only handles get
  }

  private void generateReport( final HttpServletRequest req, final HttpServletResponse resp ) throws ServletException, IOException {

    final ClassLoader classloader = this.getClass().getClassLoader();
    URL reportDefinitionURL = classloader
         .getResource( "reports/"+req.getParameter("report_file"));
    if(reportDefinitionURL == null){
      reportDefinitionURL = new File("./reports/"+req.getParameter("report_file")).toURI().toURL();
    }
    final ResourceManager resourceManager = new ResourceManager();
    System.out.println(reportDefinitionURL.getPath());
    try {
      final Resource directly = resourceManager.createDirectly( reportDefinitionURL, MasterReport.class );

      final MasterReport report = (MasterReport) directly.getResource();

      req.getParameterMap().forEach((k,v) -> {
          report.getParameterValues().put(k, v[0]);
        }
      );
      
      resp.setContentType( "application/pdf" ); 
      OutputStream out = resp.getOutputStream();
      try {
        PdfReportUtil.createPDF(report, out);
      } catch ( ReportProcessingException rpe ) {
        rpe.printStackTrace();
      } finally {
        out.close();
      }
      
    } catch (ResourceLoadingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ResourceCreationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ResourceKeyCreationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ResourceException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }


  }


  


}
