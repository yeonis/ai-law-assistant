import './App.css';
import ArticleCard from './components/ArticleCard';
import ArticleCompare from './components/ArticleCompare';
import { useLawSearch } from './hooks/useLawSearch';
import { useArticleCompare } from './hooks/useArticleCompare';
import { useState } from 'react';

function App() {
    const {
        articles,
        loading,
        error,
        hasSearched,
        page,
        totalPages,
        totalElements,
        searchedKeyword,
        handleSearch: runSearch,
        resetSearch
    } = useLawSearch();

    const {
        compareResult,
        compareLoading,
        compareError,
        handleCompare,
        resetCompare
    } = useArticleCompare();

    const [keyword, setKeyword] = useState('');
    const [expandedId, setExpandedId] = useState<number | null>(null);
    const [expandAll, setExpandAll] = useState(false);
    const getToday = () =>
        new Intl.DateTimeFormat('en-CA', {
            timeZone: 'Asia/Seoul',
            year: 'numeric',
            month: '2-digit',
            day: '2-digit'
        }).format(new Date());

    const [searchDate, setSearchDate] = useState(getToday);

    const handleSearch = async (pageNumber: number = 0) => {
        // 빈 검색어는 기존 검색과 비교 결과를 유지
        if (pageNumber === 0 && !keyword.trim()) {
            alert('검색어를 입력해주세요.');
            return;
        }
        resetCompare();

        setExpandAll(false);
        setExpandedId(null);

        await runSearch(keyword, searchDate, pageNumber);
    };


    const handleExpandAll = () => {
        setExpandAll(!expandAll);
        setExpandedId(null);
    };

    const handleReset = () => {
        resetSearch();
        resetCompare();

        setKeyword('');
        setSearchDate(getToday());

        setExpandAll(false);
        setExpandedId(null);
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

            {(compareResult || compareError || compareLoading) && (
                <section className="compare-section">
                    <div className="compare-section-header">
                        <span className="compare-section-label">
                            법령 개정 이력
                        </span>
                        <button
                            type="button"
                            className="compare-close-button"
                            aria-label="비교 화면 닫기"
                            onClick={resetCompare}
                        >
                            ✕
                        </button>
                    </div>

                    {compareLoading ? (
                        <div className="compare-notice">
                            <p>법령 개정 이력을 불러오는 중입니다.</p>
                        </div>
                    ) : compareError ? (
                        <div className="compare-notice">
                            <span className="compare-notice-icon">ⓘ</span>
                            <h3>비교할 법령 이력을 찾지 못했습니다.</h3>
                            <p>{compareError}</p>
                        </div>
                    ) : compareResult ? (
                        <ArticleCompare compareResult={compareResult} />
                    ) : null}
                </section>
            )}

            <div className="date-filter">
                <div className="date-filter-inner">
                    <label htmlFor="search-date">
                        <span className="date-icon">▦</span>
                        법령 기준일
                    </label>

                    <input
                        id="search-date"
                        type="date"
                        value={searchDate}
                        onChange={(e) => setSearchDate(e.target.value)}
                        max={getToday()}
                    />

                    <button
                        type="button"
                        className="today-button"
                        onClick={() => setSearchDate(getToday())}
                    >
                        오늘
                    </button>
                </div>
            </div>

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
                            onCompare={handleCompare}
                            compareLoading={compareLoading}
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