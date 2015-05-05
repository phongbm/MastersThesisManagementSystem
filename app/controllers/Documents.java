package controllers;

import com.avaje.ebean.Page;
import models.Document;
import models.Faculty;
import models.MastersStudent;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import play.mvc.Security;
import views.html.documents.upload;

import java.io.*;

import static play.data.Form.form;

@Security.Authenticated(Secured.class)
public class Documents extends Controller {

    public static class UploadDocumentForm {

        public FilePart document;

        public String validate() {
            MultipartFormData data = request().body().asMultipartFormData();
            document = data.getFile("document");
            if (document == null) {
                return "Tệp tin tài liệu bị thiếu!";
            }
            return null;
        }
    }

    public static Result upload() {
        String emailUser = session().get("email");
        if (Faculty.findByEmail(emailUser) != null) {
            return redirect(routes.Faculties.info(Faculty.findByEmail(emailUser)));
        }
        return ok(upload.render(form(UploadDocumentForm.class)));
    }

    public static Result delete(Long id) {
        final Document document = Document.find.byId(id);
        if (document == null) {
            return notFound(String.format("Tệp tin luận văn %s không tồn tại!", id));
        }
        MastersStudent mastersStudent = document.mastersStudent;
        Document document1 = mastersStudent.document;
        mastersStudent.document = null;
        mastersStudent.update();
        document1.delete();
        return redirect(routes.MastersThesises.list(0));
    }

    public static Result uploadDocument() {
        Form<UploadDocumentForm> form = form(UploadDocumentForm.class).bindFromRequest();
        if (form.hasErrors()) {
            return badRequest(upload.render(form));
        } else {
            Document document = new Document();
            document.name = form.get().document.getFilename();
            document.data = new byte[(int) form.get().document.getFile().length()];
            InputStream inputStream = null;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(form.get().document.getFile()));
                inputStream.read(document.data);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            document.save();
            MastersStudent mastersStudent = MastersStudent.findByEmail(session().get("email"));
            if (mastersStudent.document == null) {
                mastersStudent.document = document;
                mastersStudent.update();
            } else {
                Document document1 = mastersStudent.document;
                mastersStudent.document = document;
                mastersStudent.update();
                document1.delete();
            }
            flash("success", "Tài liệu đưa lên thành công!");
            return redirect(routes.Documents.list(0));
        }
    }

    public static Result getDocument(long id) {
        Document document = Document.find.byId(id);
        if (document != null) {
            return ok(document.data).as("document");
        } else {
            flash("error", "Tài liệu không tồn tại!");
            return redirect(routes.Documents.list(0));
        }
    }


    public static Result list(Integer page) {
        Page<Document> documentPage = Document.find(page);
        return ok(views.html.documents.list.render(documentPage));
    }

}