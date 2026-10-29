import './App.css';
import {useState} from 'react';
import ArticleCard from './components/ArticleCard';
import type { LawArticle } from './types/law'

function App() {

    const [keyword, setKeyword] = useState('');
    const [searchedKeyword, setSearchedKeyword] = useState('');
    const [articles, setArticles] = useState<LawArticle[]>([]);
    const [hasSearched, setHasSearched] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [expandedId, setExpandedId] = useState<number | null>(null);
    const [expandAll, setExpandAll] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const handleSearch = async (pageNumber:number = 0) => {
        // 공백을 제거한 검색어
        const trimmedKeyword = pageNumber === 0
            ? keyword.trim()
            : searchedKeyword;

        // 검색어가 비어 있으면 검색하지 않음
        if (!trimmedKeyword) {
            alert('검색어를 입력해주세요.');
            return;
        }

        setLoading(true);
        setError('');
        setExpandAll(false);
        setExpandedId(null);

        try{
            const response = await fetch(
                `http://localhost:8080/api/laws/articles/search?keyword=${encodeURIComponent(trimmedKeyword)}&page=${pageNumber}&size=10`,
            );

            if(!response.ok){
                throw new Error('검색 요청 실패');
            }

            const data: {
                content: LawArticle[];
                totalElements: number;
                totalPages: number;
                number: number;
            } = await response.json();

            setArticles(data.content);
            setPage(data.number);
            setTotalPages(data.totalPages);
            setTotalElements(data.totalElements);
            setSearchedKeyword(trimmedKeyword);
            setHasSearched(true);


        } catch (error) {
            console.error('검색 오류:', error);
            setError('검색 중 오류가 발생했습니다.');

            setArticles([]);
            setHasSearched(false);
        } finally {
            setLoading(false);
        }
    };

    const handleExpandAll = () => {
        setExpandAll(!expandAll);
        setExpandedId(null);
    };

    const handleReset = () => {
        setKeyword('');
        setArticles([]);
        setHasSearched(false);
        setError('');
        setExpandAll(false);
        setExpandedId(null);
        setSearchedKeyword('');
        setPage(0);
        setTotalPages(0);
        setTotalElements(0);
    };

    return (
        <div className="app">
            <div className="hero">
                <span className="hero-label">관세법령 검색 플랫폼</span>
                <h1>
                    <button
                        type="button"
                        className="home-logo"
                        onClick={handleReset}
                    >
                        LAW:<span className="hero-accent">ON</span>
                    </button>
                </h1>
                <p>복잡한 관세법령, 쉽고 명확하게.</p>
            </div>

            <form className="search-box" onSubmit={(e) => {
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
                {keyword && (
                    <button type="button" onClick={handleReset}>
                        ✕
                    </button>
                )}
                <button type="submit" disabled={loading}>
                    {loading ? '검색 중...' : '검색'}
                </button>
            </form>

            {error && <p className="error-message">{error}</p>}
            <div className="results">
                {hasSearched && !error && (
                    <div className="result-header">
                        <p className="result-count">
                            검색 결과: <strong>총 {totalElements}건</strong>
                        </p>

                        {hasSearched && articles.length > 0 && (
                            <button type="button" className="expand-all-button" onClick={handleExpandAll}>
                                {expandAll ? '전체 접기 ▲' : '전체 펼치기 ▼'}
                            </button>
                        )}
                    </div>
                )}
                {hasSearched && (articles.length > 0 ? (
                    articles.map((article) => (
                        <ArticleCard
                            key={article.articleId}
                            article={article}
                            expandedId={expandedId}
                            expandAll={expandAll}
                            setExpandedId={setExpandedId}
                            keyword={searchedKeyword}
                        />
                    ))
                ) : (
                    <div className="empty-result">
                        <div className="empty-icon">⌕</div>
                        <h3>검색 결과가 없습니다.</h3>
                        <p>다른 검색어로 다시 검색해 보세요.</p>
                    </div>
                ))}
                {hasSearched && !error && totalPages > 1 && (
                    <div className="pagination">
                        <button
                            type="button"
                            disabled={page === 0 || loading}
                            onClick={() => handleSearch(page - 1)}
                        >
                            ‹
                        </button>

                        {Array.from(
                            { length: Math.min(5, totalPages) },
                            (_, i) =>
                                Math.max(
                                    0,
                                    Math.min(totalPages - 5, page - 2)
                                ) + i
                        ).map((pageNumber) => (
                            <button
                                key={pageNumber}
                                type="button"
                                className={page === pageNumber ? 'active' : ''}
                                disabled={loading}
                                onClick={() => handleSearch(pageNumber)}
                            >
                                {pageNumber + 1}
                            </button>
                        ))}

                        <button
                            type="button"
                            disabled={page === totalPages - 1 || loading}
                            onClick={() => handleSearch(page + 1)}
                        >
                            ›
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default App;