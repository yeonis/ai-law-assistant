import './App.css';
import { useState } from 'react';

type LawArticle = {
    articleId: number;
    lawSerialNo: number;
    articleNo: number;
    articleBranchNo: number;
    articleTitle: string;
    articleContent: string;
}
function App() {

    const [keyword, setKeyword] = useState('');
    const [articles, setArticles] = useState<LawArticle[]>([]);
    const [hasSearched, setHasSearched] = useState(false);

    const handleSearch = async () => {
        // 공백을 제거한 검색어
        const trimmedKeyword = keyword.trim();

        // 검색어가 비어 있으면 검색하지 않음
        if (!trimmedKeyword) {
            alert('검색어를 입력해주세요.');
            return;
        }

        const response = await fetch(
            `http://localhost:8080/api/laws/articles/search?keyword=${encodeURIComponent(trimmedKeyword)}`
        );

        const data: LawArticle[] = await response.json();

        setArticles(data);
        setHasSearched(true);
    };

    return (
        <div className="app">
            <div className="hero">
                <span className="hero-label">법령 검색의 새로운 기준</span>
                <h1>LAW:<span className="hero-accent">ON</span></h1>
                <p>복잡한 관세법령, 쉽고 명확하게.</p>
            </div>

            <form
                className="search-box"
                onSubmit={(e) => {
                    e.preventDefault();
                    handleSearch();
                }}
            >
                <input
                    type="text"
                    placeholder="관세법령을 검색해 보세요"
                    value={keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                />
                <button type="submit">검색</button>
            </form>

            <div className="results">
                {hasSearched && (
                    articles.length > 0 ? (
                        articles.map((article) => (
                            <div key={article.articleId} className="article-card">
                                <h3>{article.articleTitle}</h3>
                                <p>{article.articleContent}</p>
                            </div>
                        ))
                    ) : (
                        <div className="empty-result">
                            <div className="empty-icon">⌕</div>
                            <h3>검색 결과가 없습니다</h3>
                            <p>다른 검색어로 다시 검색해 보세요.</p>
                        </div>
                    )
                )}
            </div>
        </div>
    );
}

export default App;