package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.dto.BankSlipResult;
import br.com.meli.projetointegrador.dto.BankSlipResultImpl;
import br.com.meli.projetointegrador.model.BankSlip;
import br.com.meli.projetointegrador.model.Customer;
import br.com.meli.projetointegrador.model.OrderStatus;
import br.com.meli.projetointegrador.model.StatusCode;
import br.com.meli.projetointegrador.repository.BankSlipRepository;
import br.com.meli.projetointegrador.repository.CustomerRepository;
import br.com.meli.projetointegrador.security.services.UserDetailsImpl;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class BankSlipService {

    private static final String HTML = "src/main/resources/bank_slip_template.html";
    private static final String PDF = "src/main/resources/bankslip_tmp.pdf";

    private BankSlipRepository bankSlipRepository;
    private CustomerRepository customerRepository;


    public BankSlipResult getBankSlip(Long orderId) {
        BankSlip bankSlip;
        bankSlip = bankSlipRepository.findByOrderStatus_Id(orderId).orElse(new BankSlip());
        if (bankSlip.getId() != null) {
            return new BankSlipResultImpl(bankSlip.getTotal(), bankSlip.getDate(), bankSlip.getName(),
                    bankSlip.getEmail(), bankSlip.getCpf());
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long userId = userDetails.getId();

        Customer customer = customerRepository.findCustomerByUser_Id(userId).orElse(new Customer());

        BankSlipResult bankSlipResult = new BankSlipResultImpl();
        bankSlipResult = bankSlipRepository.getCustomerAndCartDetailsByOrderId(orderId, customer.getId());

        if (bankSlipResult != null) {
            bankSlip.setTotal(bankSlipResult.getTotal());
            bankSlip.setDate(bankSlipResult.getDate());
            bankSlip.setName(bankSlipResult.getName());
            bankSlip.setEmail(bankSlipResult.getEmail());
            bankSlip.setCpf(bankSlipResult.getCpf());
            bankSlip.setOrderStatus(new OrderStatus(orderId, StatusCode.PURCHASE));

            bankSlipRepository.save(bankSlip);
        }

        return  bankSlipResult;
    }

    public byte[] generateBankSlipPdf(Long orderId) throws IOException, TransformerException {
        BankSlipResult bankSlipResult = getBankSlip(orderId);
        return generateHtmlToPdf(bankSlipResult);
    }

    public byte[] generateHtmlToPdf(BankSlipResult bankSlipResult) throws IOException, TransformerException {
        File inputHTML = new File(HTML);
        Document doc = createWellFormedHtml(inputHTML, bankSlipResult);
        return xhtmlToPdf(doc, PDF);
    }

    private Document createWellFormedHtml(File inputHTML, BankSlipResult bankSlipResult) throws IOException {
        Document doc = Jsoup.parse(inputHTML, "UTF-8");
        Element link = doc.select("p#interestRate").first();
        System.out.println(link.text());
        link.appendText("JJ");
        link.text("1234.55");

        doc.select("p#interestRate").attr("text", "11");
        doc.outputSettings()
                .syntax(Document.OutputSettings.Syntax.xml);
        return doc;
    }

    private byte[] xhtmlToPdf(Document doc, String outputPdf) throws IOException, TransformerException {
        OutputStream os = new FileOutputStream(outputPdf);

        String baseUri = FileSystems.getDefault()
                .getPath("src/main/resources/")
                .toUri()
                .toString();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withUri(outputPdf);
        builder.toStream(os);
        builder.withW3cDocument(new W3CDom().fromJsoup(doc), baseUri);
        builder.run();


        return Files.readAllBytes(Paths.get(outputPdf));
    }
}
