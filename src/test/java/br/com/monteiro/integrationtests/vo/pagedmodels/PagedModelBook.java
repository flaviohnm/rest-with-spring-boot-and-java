package br.com.monteiro.integrationtests.vo.pagedmodels;

import br.com.monteiro.integrationtests.vo.BookVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement
public class PagedModelBook {
    @XmlElement(name = "content")
    private List<BookVO> content;

    public PagedModelBook() {
    }

    public PagedModelBook(List<BookVO> content) {
        this.content = content;
    }

    public List<BookVO> getContent() {
        return content;
    }

    public void setContent(List<BookVO> content) {
        this.content = content;
    }

}

