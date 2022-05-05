package br.com.meli.projetointegrador.service;

import br.com.meli.projetointegrador.dto.BankSlipResult;
import br.com.meli.projetointegrador.dto.BankSlipResultImpl;
import br.com.meli.projetointegrador.exception.UserHasNoOrder;
import br.com.meli.projetointegrador.exception.UserHasNoPurchaseException;
import br.com.meli.projetointegrador.model.*;
import br.com.meli.projetointegrador.repository.BankSlipRepository;
import br.com.meli.projetointegrador.repository.CartRepository;
import br.com.meli.projetointegrador.repository.CustomerRepository;
import br.com.meli.projetointegrador.security.services.UserDetailsImpl;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.AllArgsConstructor;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class BankSlipService {

    private static final String HTML = "src/main/resources/bank_slip_template.html";
    private static final String PDF = "src/main/resources/bankslip_tmp.pdf";
    private static final int SLIP_FINAL_PART_LENGTH = 10;
    private static final Font BARCODE_TEXT_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final String originalBankSlipCode = "34591.12345 12345.691112 12022.370008 8";

    private BankSlipRepository bankSlipRepository;
    private CustomerRepository customerRepository;
    private CartRepository cartRepository;


    public List<Cart> getPurchasedCartsByCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        Long userId = userDetails.getId();

        Customer customer = customerRepository.findCustomerByUser_Id(userId).orElse(new Customer());

        List<Cart> carts = cartRepository.getAllByCustomerIdAndOrderStatus_StatusCode(customer.getId(), StatusCode.PURCHASE);

        if (carts.size() < 1) {
            throw new UserHasNoPurchaseException("This User has no Purchases!");
        }
        return carts;
    }

    public BankSlipResult getBankSlip(Long orderId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        String email = userDetails.getEmail();

        getPurchasedCartsByCustomer();

        BankSlip bankSlip;
        bankSlip = bankSlipRepository.findByOrderStatus_IdAndEmailEquals(orderId, email).orElse(new BankSlip());
        if (bankSlip.getId() != null) {
            return new BankSlipResultImpl(bankSlip.getTotal(), bankSlip.getDate(), bankSlip.getName(),
                    bankSlip.getEmail(), bankSlip.getCpf());
        }

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
        } else {
            throw new UserHasNoOrder("User has no Order with this Id!");
        }

        return  bankSlipResult;
    }

    public byte[] generateBankSlipPdf(Long orderId) throws Exception {
        BankSlipResult bankSlipResult = getBankSlip(orderId);
        return generateHtmlToPdf(bankSlipResult);
    }

    public byte[] generateHtmlToPdf(BankSlipResult bankSlipResult) throws Exception {
        File inputHTML = new File(HTML);
        Document doc = createWellFormedHtml(inputHTML, bankSlipResult);
        return xhtmlToPdf(doc, PDF);
    }

    private void replaceHtmlInputs(Document doc, String identifier, String value) {
        //doc.select("p#interestRate").attr("text", "11");
        Element el = doc.select(identifier).first();
        if (el != null)
            el.text(value);
    }

    private void replaceHtmlElemAttr(Element el, String value, String attribute) {
        String srcValue = el.attr(attribute);
        el.attr("src", value);
    }

    private String createBankSlipCode(String initialValue, String date, Double value) {
        LocalDate processingDate = LocalDate.parse(date);
        LocalDate baseDate = LocalDate.of(1997, 7, 10);
        long daysElapsed = ChronoUnit.DAYS.between(baseDate, processingDate);
        String daysElapsedStr = String.valueOf(daysElapsed);

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String textBankSlipValue = decimalFormat.format(value).replace(".", "").replace(",", "");

        String formattedValue = String.format("%1$" + SLIP_FINAL_PART_LENGTH + "s", textBankSlipValue)
                .replace(' ', '0');

        return initialValue + " " + daysElapsedStr + formattedValue;

    }

    public BufferedImage generateQRcodeB64(Long orderId) throws Exception {

        BankSlipResult bankSlipResult = new BankSlipResultImpl();
        bankSlipResult = getBankSlip(orderId);
        String bankSlipCode = createBankSlipCode(originalBankSlipCode, bankSlipResult.getDate(), bankSlipResult.getTotal());

        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = barcodeWriter.encode(bankSlipCode, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }


    public BufferedImage generateBarcodeB64(Long orderId) throws Exception {

        BankSlipResult bankSlipResult = new BankSlipResultImpl();
        bankSlipResult = getBankSlip(orderId);
        String bankSlipCode = createBankSlipCode(originalBankSlipCode, bankSlipResult.getDate(), bankSlipResult.getTotal());

        Barcode barcode = BarcodeFactory.createCode128(bankSlipCode);
        barcode.setFont(BARCODE_TEXT_FONT);

        return BarcodeImageHandler.getImage(barcode);
    }

    public String generateBarcodeB64String(String barcodeText) throws Exception {
        Barcode barcode = BarcodeFactory.createCode128(barcodeText);
        barcode.setFont(BARCODE_TEXT_FONT);
        BufferedImage bufImg = BarcodeImageHandler.getImage(barcode);

        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try (final OutputStream b64os = Base64.getEncoder().wrap(os)) {
            ImageIO.write(bufImg, "png", b64os);
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }

        return os.toString();
    }

    private Document createWellFormedHtml(File inputHTML, BankSlipResult bankSlipResult) throws Exception {
        Document doc = Jsoup.parse(inputHTML, "UTF-8");
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        if (bankSlipResult == null) {
            return doc;
        }

        Element el = doc.select("span#bankSlipCode").first();
        String bankSlipCode = el.text();
        bankSlipCode = createBankSlipCode(bankSlipCode, bankSlipResult.getDate(), bankSlipResult.getTotal());

        el = doc.select("img#barcodeImg").first();
        String barcodeB64 = generateBarcodeB64String(bankSlipCode);
        replaceHtmlElemAttr(el, "data:image/png;base64," + barcodeB64, "src");

        replaceHtmlInputs(doc, "span#bankSlipCode", bankSlipCode);
        replaceHtmlInputs(doc, "p#totalValueDocument", bankSlipResult.getTotal().toString());
        replaceHtmlInputs(doc, "div#buyerName", bankSlipResult.getName());
        replaceHtmlInputs(doc, "div#buyerCpfCnpj", bankSlipResult.getCpf());
        replaceHtmlInputs(doc, "div#buyerEmail", bankSlipResult.getEmail());

        LocalDate date = LocalDate.parse(bankSlipResult.getDate());
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateFormatted = date.format(formatters);

        replaceHtmlInputs(doc, "p#bankSlipDueDate", dateFormatted);
        replaceHtmlInputs(doc, "p#bankSlipDate", dateFormatted);
        replaceHtmlInputs(doc, "p#bankSlipProcessingDate", dateFormatted);

        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
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
