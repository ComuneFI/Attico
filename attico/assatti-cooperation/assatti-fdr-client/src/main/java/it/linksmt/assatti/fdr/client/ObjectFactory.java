
package it.linksmt.assatti.fdr.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.linksmt.assatti.fdr.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Auth_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "auth");
    private final static QName _ConfigurationResponse_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "configurationResponse");
    private final static QName _FdrParam_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "fdrParam");
    private final static QName _FdrWsResponse_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "fdrWsResponse");
    private final static QName _MarkParam_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "markParam");
    private final static QName _PadesParam_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "padesParam");
    private final static QName _SignFiles_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "signFiles");
    private final static QName _SignPdfFiles_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "signPdfFiles");
    private final static QName _XadesParam_QNAME = new QName("http://service.ws.fdr.linksmt.it/", "xadesParam");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.linksmt.assatti.fdr.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConfigurationResponse }
     * 
     */
    public ConfigurationResponse createConfigurationResponse() {
        return new ConfigurationResponse();
    }

    /**
     * Create an instance of {@link FdrParam }
     * 
     */
    public FdrParam createFdrParam() {
        return new FdrParam();
    }

    /**
     * Create an instance of {@link FdrWsResponse }
     * 
     */
    public FdrWsResponse createFdrWsResponse() {
        return new FdrWsResponse();
    }

    /**
     * Create an instance of {@link MarkParam }
     * 
     */
    public MarkParam createMarkParam() {
        return new MarkParam();
    }

    /**
     * Create an instance of {@link PadesParam }
     * 
     */
    public PadesParam createPadesParam() {
        return new PadesParam();
    }

    /**
     * Create an instance of {@link SignFiles }
     * 
     */
    public SignFiles createSignFiles() {
        return new SignFiles();
    }

    /**
     * Create an instance of {@link SignPdfFiles }
     * 
     */
    public SignPdfFiles createSignPdfFiles() {
        return new SignPdfFiles();
    }

    /**
     * Create an instance of {@link XadesParam }
     * 
     */
    public XadesParam createXadesParam() {
        return new XadesParam();
    }

    /**
     * Create an instance of {@link SignatureAuth }
     * 
     */
    public SignatureAuth createSignatureAuth() {
        return new SignatureAuth();
    }

    /**
     * Create an instance of {@link BaseResponse }
     * 
     */
    public BaseResponse createBaseResponse() {
        return new BaseResponse();
    }

    /**
     * Create an instance of {@link FileElement }
     * 
     */
    public FileElement createFileElement() {
        return new FileElement();
    }

    /**
     * Create an instance of {@link ElencoErrori }
     * 
     */
    public ElencoErrori createElencoErrori() {
        return new ElencoErrori();
    }

    /**
     * Create an instance of {@link ErrorResponse }
     * 
     */
    public ErrorResponse createErrorResponse() {
        return new ErrorResponse();
    }

    /**
     * Create an instance of {@link MarkParamFiles }
     * 
     */
    public MarkParamFiles createMarkParamFiles() {
        return new MarkParamFiles();
    }

    /**
     * Create an instance of {@link Profilo }
     * 
     */
    public Profilo createProfilo() {
        return new Profilo();
    }

    /**
     * Create an instance of {@link Utente }
     * 
     */
    public Utente createUtente() {
        return new Utente();
    }

    /**
     * Create an instance of {@link Transform }
     * 
     */
    public Transform createTransform() {
        return new Transform();
    }

    /**
     * Create an instance of {@link XadesTransform }
     * 
     */
    public XadesTransform createXadesTransform() {
        return new XadesTransform();
    }

    /**
     * Create an instance of {@link VerificationAuth }
     * 
     */
    public VerificationAuth createVerificationAuth() {
        return new VerificationAuth();
    }

    /**
     * Create an instance of {@link PdfFile }
     * 
     */
    public PdfFile createPdfFile() {
        return new PdfFile();
    }

    /**
     * Create an instance of {@link SupportAuth }
     * 
     */
    public SupportAuth createSupportAuth() {
        return new SupportAuth();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "auth")
    public JAXBElement<Object> createAuth(Object value) {
        return new JAXBElement<Object>(_Auth_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConfigurationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "configurationResponse")
    public JAXBElement<ConfigurationResponse> createConfigurationResponse(ConfigurationResponse value) {
        return new JAXBElement<ConfigurationResponse>(_ConfigurationResponse_QNAME, ConfigurationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FdrParam }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "fdrParam")
    public JAXBElement<FdrParam> createFdrParam(FdrParam value) {
        return new JAXBElement<FdrParam>(_FdrParam_QNAME, FdrParam.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FdrWsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "fdrWsResponse")
    public JAXBElement<FdrWsResponse> createFdrWsResponse(FdrWsResponse value) {
        return new JAXBElement<FdrWsResponse>(_FdrWsResponse_QNAME, FdrWsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MarkParam }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "markParam")
    public JAXBElement<MarkParam> createMarkParam(MarkParam value) {
        return new JAXBElement<MarkParam>(_MarkParam_QNAME, MarkParam.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PadesParam }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "padesParam")
    public JAXBElement<PadesParam> createPadesParam(PadesParam value) {
        return new JAXBElement<PadesParam>(_PadesParam_QNAME, PadesParam.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignFiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "signFiles")
    public JAXBElement<SignFiles> createSignFiles(SignFiles value) {
        return new JAXBElement<SignFiles>(_SignFiles_QNAME, SignFiles.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SignPdfFiles }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "signPdfFiles")
    public JAXBElement<SignPdfFiles> createSignPdfFiles(SignPdfFiles value) {
        return new JAXBElement<SignPdfFiles>(_SignPdfFiles_QNAME, SignPdfFiles.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XadesParam }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service.ws.fdr.linksmt.it/", name = "xadesParam")
    public JAXBElement<XadesParam> createXadesParam(XadesParam value) {
        return new JAXBElement<XadesParam>(_XadesParam_QNAME, XadesParam.class, null, value);
    }

}
